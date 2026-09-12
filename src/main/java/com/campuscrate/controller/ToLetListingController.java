package com.campuscrate.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.dto.ToLetListingRequest;
import com.campuscrate.dto.ToLetListingResponse;
import com.campuscrate.security.CurrentUser;
import com.campuscrate.service.ToLetListingService;

@RestController
@RequestMapping("/api/to-let/listings")
public class ToLetListingController {
    private final ToLetListingService listingService;
    private final CurrentUser currentUser;
    public ToLetListingController(ToLetListingService listingService, CurrentUser currentUser) {
        this.listingService = listingService; this.currentUser = currentUser;
    }
    @PostMapping
    public ResponseEntity<ToLetListingResponse> create(@Valid @RequestBody ToLetListingRequest request) {
        currentUser.requireUser(request.ownerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(listingService.create(request));
    }
    @GetMapping
    public List<ToLetListingResponse> findAll(@RequestParam(required = false) String search,
            @RequestParam(required = false) String area, @RequestParam(required = false) BigDecimal maxRent) {
        return listingService.findAll(search, area, maxRent);
    }
    @GetMapping("/{listingId}") public ToLetListingResponse findById(@PathVariable Long listingId) { return listingService.findById(listingId); }
    @GetMapping("/owners/{ownerId}") public List<ToLetListingResponse> findByOwner(@PathVariable Long ownerId) { return listingService.findByOwner(ownerId); }
    @PutMapping("/{listingId}")
    public ToLetListingResponse update(@PathVariable Long listingId, @RequestParam Long ownerId, @Valid @RequestBody ToLetListingRequest request) {
        currentUser.requireUser(ownerId); currentUser.requireUser(request.ownerId());
        return listingService.update(listingId, ownerId, request);
    }
    @DeleteMapping("/{listingId}")
    public ResponseEntity<Void> close(@PathVariable Long listingId, @RequestParam Long ownerId) {
        currentUser.requireUser(ownerId); listingService.close(listingId, ownerId); return ResponseEntity.noContent().build();
    }
}
