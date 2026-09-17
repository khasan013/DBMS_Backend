package com.campuscrate.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.ToLetListingRequest;
import com.campuscrate.dto.ToLetListingResponse;
import com.campuscrate.exception.ToLetConflictException;
import com.campuscrate.exception.ToLetForbiddenException;
import com.campuscrate.exception.ToLetInvalidRequestException;
import com.campuscrate.exception.ToLetListingNotFoundException;
import com.campuscrate.model.ToLetListing;
import com.campuscrate.repository.ToLetListingRepository;
import com.campuscrate.repository.UserRepository;

@Service
public class ToLetListingService {
    private final ToLetListingRepository listingRepository;
    private final UserRepository userRepository;

    public ToLetListingService(ToLetListingRepository listingRepository, UserRepository userRepository) {
        this.listingRepository = listingRepository; this.userRepository = userRepository;
    }

    @Transactional
    public ToLetListingResponse create(ToLetListingRequest request) {
        requireOwner(request.ownerId());
        ToLetListing created = listingRepository.create(fromRequest(null, request, "AVAILABLE"));
        return toResponse(find(created.listingId()));
    }

    public List<ToLetListingResponse> findAll(String search, String area, BigDecimal maxRent) {
        if (maxRent != null && maxRent.signum() < 0) throw new ToLetInvalidRequestException("Maximum rent cannot be negative");
        return listingRepository.findAll(search, area, maxRent, false, null).stream().map(this::toResponse).toList();
    }

    public ToLetListingResponse findById(Long listingId) { return toResponse(find(listingId)); }

    @Transactional
    public ToLetListingResponse updateStatusByAdmin(Long listingId, String status) {
        String normalized = status.trim().toUpperCase();
        if (!Set.of("AVAILABLE", "RENTED", "CLOSED").contains(normalized)) {
            throw new ToLetInvalidRequestException("To-let status must be AVAILABLE, RENTED, or CLOSED");
        }
        find(listingId);
        listingRepository.updateStatus(listingId, normalized);
        return findById(listingId);
    }

    public List<ToLetListingResponse> findAllForAdmin() {
        return listingRepository.findAll(null, null, null, false, null).stream().map(this::toResponse).toList();
    }

    public List<ToLetListingResponse> findByOwner(Long ownerId) {
        requireOwner(ownerId);
        return listingRepository.findAll(null, null, null, false, ownerId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ToLetListingResponse update(Long listingId, Long ownerId, ToLetListingRequest request) {
        if (!ownerId.equals(request.ownerId())) {
            throw new ToLetForbiddenException("Owner ID does not match the authenticated user");
        }
        ToLetListing existing = find(listingId);
        verifyOwner(existing, ownerId);
        if (!"AVAILABLE".equals(existing.status())) throw new ToLetConflictException("Only available listings can be edited");
        listingRepository.update(fromRequest(listingId, request, existing.status()));
        return toResponse(find(listingId));
    }

    @Transactional
    public void close(Long listingId, Long ownerId) {
        ToLetListing existing = find(listingId);
        verifyOwner(existing, ownerId);
        if (!"AVAILABLE".equals(existing.status())) throw new ToLetConflictException("Listing is already closed");
        listingRepository.close(listingId);
    }

    private ToLetListing fromRequest(Long listingId, ToLetListingRequest request, String status) {
        return new ToLetListing(listingId, request.ownerId(), request.title().trim(), request.description().trim(), request.area().trim(),
                request.monthlyRent(), request.bedrooms(), request.bathrooms(), request.contactPhone().trim(), request.availableFrom(),
                status, null, null, request.photoUrls() == null ? List.of() : request.photoUrls());
    }
    private ToLetListing find(Long listingId) { return listingRepository.findById(listingId).orElseThrow(() -> new ToLetListingNotFoundException(listingId)); }
    private void requireOwner(Long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) throw new ToLetInvalidRequestException("Owner not found: " + ownerId);
    }
    private void verifyOwner(ToLetListing listing, Long ownerId) {
        if (!listing.ownerId().equals(ownerId)) throw new ToLetForbiddenException("You can modify only your own to-let listings");
    }
    private ToLetListingResponse toResponse(ToLetListing listing) {
        return new ToLetListingResponse(listing.listingId(), listing.ownerId(), listing.title(), listing.description(), listing.area(),
                listing.monthlyRent(), listing.bedrooms(), listing.bathrooms(), listing.contactPhone(), listing.availableFrom(),
                listing.status(), listing.createdAt(), listing.updatedAt(), listing.photoUrls());
    }
}
