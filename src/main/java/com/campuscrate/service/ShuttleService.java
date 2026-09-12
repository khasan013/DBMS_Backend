package com.campuscrate.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.*;
import com.campuscrate.exception.*;
import com.campuscrate.model.ShuttleDriver;
import com.campuscrate.model.ShuttleTrip;
import com.campuscrate.model.ShuttleWaitRequest;
import com.campuscrate.repository.ShuttleDriverRepository;
import com.campuscrate.repository.ShuttleTripRepository;
import com.campuscrate.repository.ShuttleWaitRequestRepository;
import com.campuscrate.repository.UserRepository;

@Service
public class ShuttleService {

    private static final Set<String> ROUTES = Set.of("Kuril", "Notun Bazar", "Aftab-Nagor");
    private static final Set<String> DRIVER_DECISIONS = Set.of("approved", "rejected");
    private static final Set<String> WAIT_TRANSITIONS = Set.of("accepted", "rejected", "inside_shuttle", "picked");

    private final ShuttleDriverRepository driverRepository;
    private final ShuttleTripRepository tripRepository;
    private final ShuttleWaitRequestRepository waitRequestRepository;
    private final UserRepository userRepository;

    public ShuttleService(ShuttleDriverRepository driverRepository, ShuttleTripRepository tripRepository,
            ShuttleWaitRequestRepository waitRequestRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.waitRequestRepository = waitRequestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShuttleDriverResponse applyAsDriver(ShuttleDriverRequest request) {
        requireUser(request.userId(), "user");
        if (driverRepository.existsByUserId(request.userId())) {
            throw new ShuttleConflictException("A shuttle driver application already exists for this user");
        }
        try {
            ShuttleDriver driver = driverRepository.create(new ShuttleDriver(null, request.userId(), "pending",
                    request.phone(), request.vehicleName(), request.vehicleNumber(), request.profileInfo(),
                    null, null, null, null));
            return toResponse(findDriverById(driver.getDriverId()));
        } catch (DataIntegrityViolationException exception) {
            throw new ShuttleConflictException("A shuttle driver application already exists for this user");
        }
    }

    public List<ShuttleDriverResponse> findDrivers() {
        return driverRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ShuttleDriverResponse findDriverByUserId(Long userId) {
        return toResponse(driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ShuttleNotFoundException("driver for user", userId)));
    }

    @Transactional
    public ShuttleDriverResponse decideDriver(Long userId, ShuttleDriverStatusRequest request) {
        if (!DRIVER_DECISIONS.contains(request.status())) {
            throw new ShuttleInvalidRequestException("Driver status must be approved or rejected");
        }
        requireUser(request.approvedByUserId(), "approving user");
        findDriverByUserId(userId);
        if (!driverRepository.updateStatus(userId, request.status(), request.approvedByUserId())) {
            throw new ShuttleConflictException("Only pending driver applications can be decided");
        }
        return findDriverByUserId(userId);
    }

    @Transactional
    public ShuttleTripResponse createTrip(ShuttleTripRequest request) {
        if (!ROUTES.contains(request.route())) {
            throw new ShuttleInvalidRequestException("Route must be Kuril, Notun Bazar, or Aftab-Nagor");
        }
        ShuttleDriver driver = driverRepository.findByUserId(request.driverId())
                .orElseThrow(() -> new ShuttleNotFoundException("driver for user", request.driverId()));
        if (!"approved".equals(driver.getStatus())) {
            throw new ShuttleForbiddenException("Only approved shuttle drivers can create trips");
        }
        if (tripRepository.findOpenByDriverId(request.driverId()).isPresent()) {
            throw new ShuttleConflictException("A driver can have only one open trip");
        }
        ShuttleTrip trip = tripRepository.create(request.driverId(), request.route(), request.startNow());
        if (trip.getTripId() == null) {
            throw new ShuttleConflictException("Could not create shuttle trip");
        }
        return toResponse(findTrip(trip.getTripId()));
    }

    public ShuttleTripResponse findTripById(Long tripId) {
        return toResponse(findTrip(tripId));
    }

    public List<ShuttleTripResponse> findTrips(String route, String status) {
        if (!ROUTES.contains(route)) {
            throw new ShuttleInvalidRequestException("Route must be Kuril, Notun Bazar, or Aftab-Nagor");
        }
        if (!Set.of("scheduled", "active", "in_progress", "completed", "cancelled").contains(status)) {
            throw new ShuttleInvalidRequestException("Invalid trip status");
        }
        return tripRepository.findByRouteAndStatus(route, status).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ShuttleTripResponse updateTripLocation(Long tripId, ShuttleLocationRequest request) {
        ShuttleTrip trip = findTrip(tripId);
        requireDriverOwnership(trip, request.driverId());
        if (!tripRepository.updateLocation(tripId, request.driverId(), request.latitude(), request.longitude(), LocalDateTime.now())) {
            throw new ShuttleConflictException("Only open trips can receive location updates");
        }
        return toResponse(findTrip(tripId));
    }

    @Transactional
    public ShuttleTripResponse updateTripStatus(Long tripId, ShuttleTripStatusRequest request) {
        ShuttleTrip trip = findTrip(tripId);
        requireDriverOwnership(trip, request.driverId());
        String nextStatus = request.status();
        if (!isAllowedTripTransition(trip.getStatus(), nextStatus)) {
            throw new ShuttleInvalidRequestException("Invalid trip status transition from " + trip.getStatus() + " to " + nextStatus);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startedAt = "in_progress".equals(nextStatus) ? (trip.getStartedAt() == null ? now : trip.getStartedAt()) : trip.getStartedAt();
        LocalDateTime endedAt = "completed".equals(nextStatus) ? now : null;
        if (!tripRepository.updateStatus(tripId, request.driverId(), nextStatus, startedAt, endedAt)) {
            throw new ShuttleConflictException("Trip status could not be updated");
        }
        if ("completed".equals(nextStatus)) {
            waitRequestRepository.cancelWaitingRequestsForTrip(tripId);
        }
        return toResponse(findTrip(tripId));
    }

    @Transactional
    public void cancelTrip(Long tripId, Long driverId) {
        ShuttleTrip trip = findTrip(tripId);
        requireDriverOwnership(trip, driverId);
        if (!tripRepository.cancelScheduledTrip(tripId, driverId)) {
            throw new ShuttleConflictException("Only scheduled trips can be cancelled");
        }
        waitRequestRepository.cancelWaitingRequestsForTrip(tripId);
    }

    @Transactional
    public ShuttleWaitRequestResponse createWaitRequest(ShuttleWaitRequestRequest request) {
        requireUser(request.userId(), "user");
        ShuttleTrip trip = findTrip(request.tripId());
        if (!trip.getDriverId().equals(request.driverId())) {
            throw new ShuttleInvalidRequestException("Driver does not operate the selected trip");
        }
        if (trip.getDriverId().equals(request.userId())) {
            throw new ShuttleForbiddenException("A driver cannot request their own shuttle");
        }
        if (!Set.of("scheduled", "active", "in_progress").contains(trip.getStatus())) {
            throw new ShuttleConflictException("Wait requests are allowed only for open trips");
        }
        ShuttleWaitRequest waitRequest = waitRequestRepository.create(new ShuttleWaitRequest(null, request.tripId(),
                request.driverId(), request.userId(), request.latitude(), request.longitude(), "waiting", null,
                null, null, null));
        if (waitRequest.getWaitRequestId() == null) {
            throw new ShuttleConflictException("Could not create shuttle wait request");
        }
        return toResponse(findWaitRequest(waitRequest.getWaitRequestId()));
    }

    public List<ShuttleWaitRequestResponse> findWaitRequests(Long tripId) {
        findTrip(tripId);
        return waitRequestRepository.findByTripId(tripId).stream().map(this::toResponse).toList();
    }

    public List<ShuttleWaitRequestResponse> findDriverWaitingRequests(Long driverId) {
        return waitRequestRepository.findByDriverIdAndStatus(driverId, "waiting").stream().map(this::toResponse).toList();
    }

    @Transactional
    public ShuttleWaitRequestResponse updateWaitRequestStatus(Long waitRequestId, ShuttleWaitRequestStatusRequest request) {
        ShuttleWaitRequest waitRequest = findWaitRequest(waitRequestId);
        if (!waitRequest.getDriverId().equals(request.driverId())) {
            throw new ShuttleForbiddenException("Only the trip driver can update this wait request");
        }
        if (!WAIT_TRANSITIONS.contains(request.status()) || !isAllowedWaitTransition(waitRequest.getStatus(), request.status())) {
            throw new ShuttleInvalidRequestException("Invalid wait request status transition");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime decisionAt = Set.of("accepted", "rejected").contains(request.status()) ? now : waitRequest.getDecisionAt();
        LocalDateTime insideAt = Set.of("inside_shuttle", "picked").contains(request.status()) ? now : waitRequest.getInsideAt();
        waitRequestRepository.updateStatus(waitRequestId, request.status(), decisionAt, insideAt);
        return toResponse(findWaitRequest(waitRequestId));
    }

    private boolean isAllowedTripTransition(String current, String next) {
        return ("scheduled".equals(current) && Set.of("active", "in_progress").contains(next))
                || ("active".equals(current) && "in_progress".equals(next))
                || ("in_progress".equals(current) && "completed".equals(next));
    }

    private boolean isAllowedWaitTransition(String current, String next) {
        return ("waiting".equals(current) && Set.of("accepted", "rejected").contains(next))
                || ("accepted".equals(current) && Set.of("inside_shuttle", "picked").contains(next));
    }

    private void requireUser(Long userId, String role) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ShuttleNotFoundException(role, userId);
        }
    }

    private void requireDriverOwnership(ShuttleTrip trip, Long driverId) {
        if (!trip.getDriverId().equals(driverId)) {
            throw new ShuttleForbiddenException("Only the trip driver can perform this action");
        }
    }

    private ShuttleDriver findDriverById(Long driverId) {
        return driverRepository.findById(driverId).orElseThrow(() -> new ShuttleNotFoundException("driver", driverId));
    }

    private ShuttleTrip findTrip(Long tripId) {
        return tripRepository.findById(tripId).orElseThrow(() -> new ShuttleNotFoundException("trip", tripId));
    }

    private ShuttleWaitRequest findWaitRequest(Long waitRequestId) {
        return waitRequestRepository.findById(waitRequestId)
                .orElseThrow(() -> new ShuttleNotFoundException("wait request", waitRequestId));
    }

    private ShuttleDriverResponse toResponse(ShuttleDriver driver) {
        return new ShuttleDriverResponse(driver.getDriverId(), driver.getUserId(), driver.getStatus(), driver.getPhone(),
                driver.getVehicleName(), driver.getVehicleNumber(), driver.getProfileInfo(), driver.getApprovedBy(),
                driver.getApprovedAt(), driver.getCreatedAt(), driver.getUpdatedAt());
    }

    private ShuttleTripResponse toResponse(ShuttleTrip trip) {
        return new ShuttleTripResponse(trip.getTripId(), trip.getDriverId(), trip.getRoute(), trip.getStatus(),
                trip.getScheduledStartAt(), trip.getStartedAt(), trip.getEndedAt(), trip.getLatitude(), trip.getLongitude(),
                trip.getLastLocationAt(), trip.getCreatedAt(), trip.getUpdatedAt());
    }

    private ShuttleWaitRequestResponse toResponse(ShuttleWaitRequest request) {
        return new ShuttleWaitRequestResponse(request.getWaitRequestId(), request.getTripId(), request.getDriverId(),
                request.getUserId(), request.getLatitude(), request.getLongitude(), request.getStatus(),
                request.getDecisionAt(), request.getInsideAt(), request.getCreatedAt(), request.getUpdatedAt());
    }
}
