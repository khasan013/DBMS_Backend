package com.campuscrate.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.LocationRequest;
import com.campuscrate.dto.LocationResponse;
import com.campuscrate.exception.DuplicateLocationException;
import com.campuscrate.exception.LocationNotFoundException;
import com.campuscrate.model.Location;
import com.campuscrate.repository.LocationRepository;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<LocationResponse> findAll() {
        return locationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public LocationResponse findById(Long locationId) {
        return toResponse(findLocation(locationId));
    }

    @Transactional
    public LocationResponse create(LocationRequest request) {
        if (locationRepository.existsByName(request.name())) {
            throw new DuplicateLocationException(request.name());
        }

        try {
            return toResponse(locationRepository.create(new Location(null, request.name())));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateLocationException(request.name());
        }
    }

    @Transactional
    public LocationResponse update(Long locationId, LocationRequest request) {
        findLocation(locationId);
        if (locationRepository.existsByNameExceptId(request.name(), locationId)) {
            throw new DuplicateLocationException(request.name());
        }

        try {
            locationRepository.update(locationId, request.name());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateLocationException(request.name());
        }
        return new LocationResponse(locationId, request.name());
    }

    @Transactional
    public void delete(Long locationId) {
        findLocation(locationId);
        locationRepository.delete(locationId);
    }

    private Location findLocation(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));
    }

    private LocationResponse toResponse(Location location) {
        return new LocationResponse(location.getLocationId(), location.getName());
    }
}