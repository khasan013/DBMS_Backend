package com.campuscrate.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.campuscrate.dto.*;
import com.campuscrate.service.ShuttleService;
import com.campuscrate.security.CurrentUser;

@RestController
@RequestMapping("/api/shuttle")
public class ShuttleController {

    private final ShuttleService shuttleService;
    private final CurrentUser currentUser;

    public ShuttleController(ShuttleService shuttleService, CurrentUser currentUser) {
        this.shuttleService = shuttleService;
        this.currentUser = currentUser;
    }

    @PostMapping("/drivers")
    public ResponseEntity<ShuttleDriverResponse> applyAsDriver(@Valid @RequestBody ShuttleDriverRequest request) {
        currentUser.requireUser(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(shuttleService.applyAsDriver(request));
    }

    @GetMapping("/drivers")
    public List<ShuttleDriverResponse> findDrivers() {
        return shuttleService.findDrivers();
    }

    @GetMapping("/drivers/users/{userId}")
    public ShuttleDriverResponse findDriver(@PathVariable Long userId) {
        return shuttleService.findDriverByUserId(userId);
    }

    @PutMapping("/drivers/users/{userId}/status")
    public ShuttleDriverResponse decideDriver(@PathVariable Long userId,
            @Valid @RequestBody ShuttleDriverStatusRequest request) {
        return shuttleService.decideDriver(userId, request);
    }

    @PostMapping("/trips")
    public ResponseEntity<ShuttleTripResponse> createTrip(@Valid @RequestBody ShuttleTripRequest request) {
        currentUser.requireUser(request.driverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(shuttleService.createTrip(request));
    }

    @GetMapping("/trips")
    public List<ShuttleTripResponse> findTrips(@RequestParam String route, @RequestParam String status) {
        return shuttleService.findTrips(route, status);
    }

    @GetMapping("/trips/{tripId}")
    public ShuttleTripResponse findTrip(@PathVariable Long tripId) {
        return shuttleService.findTripById(tripId);
    }

    @PutMapping("/trips/{tripId}/location")
    public ShuttleTripResponse updateLocation(@PathVariable Long tripId,
            @Valid @RequestBody ShuttleLocationRequest request) {
        currentUser.requireUser(request.driverId());
        return shuttleService.updateTripLocation(tripId, request);
    }

    @PutMapping("/trips/{tripId}/status")
    public ShuttleTripResponse updateTripStatus(@PathVariable Long tripId,
            @Valid @RequestBody ShuttleTripStatusRequest request) {
        currentUser.requireUser(request.driverId());
        return shuttleService.updateTripStatus(tripId, request);
    }

    @DeleteMapping("/trips/{tripId}")
    public ResponseEntity<Void> cancelTrip(@PathVariable Long tripId, @RequestParam Long driverId) {
        currentUser.requireUser(driverId);
        shuttleService.cancelTrip(tripId, driverId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/wait-requests")
    public ResponseEntity<ShuttleWaitRequestResponse> createWaitRequest(
            @Valid @RequestBody ShuttleWaitRequestRequest request) {
        currentUser.requireUser(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(shuttleService.createWaitRequest(request));
    }

    @GetMapping("/trips/{tripId}/wait-requests")
    public List<ShuttleWaitRequestResponse> findWaitRequests(@PathVariable Long tripId) {
        return shuttleService.findWaitRequests(tripId);
    }

    @GetMapping("/drivers/users/{driverId}/wait-requests")
    public List<ShuttleWaitRequestResponse> findDriverWaitingRequests(@PathVariable Long driverId) {
        return shuttleService.findDriverWaitingRequests(driverId);
    }

    @PutMapping("/wait-requests/{waitRequestId}/status")
    public ShuttleWaitRequestResponse updateWaitRequestStatus(@PathVariable Long waitRequestId,
            @Valid @RequestBody ShuttleWaitRequestStatusRequest request) {
        currentUser.requireUser(request.driverId());
        return shuttleService.updateWaitRequestStatus(waitRequestId, request);
    }
}
