package com.campuscrate.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.dto.ClaimRequest;
import com.campuscrate.dto.ClaimResponse;
import com.campuscrate.dto.ClaimStatusUpdateRequest;
import com.campuscrate.service.ClaimService;
import com.campuscrate.security.CurrentUser;

@RestController
public class ClaimController {

    private final ClaimService claimService;
    private final CurrentUser currentUser;

    public ClaimController(ClaimService claimService, CurrentUser currentUser) {
        this.claimService = claimService;
        this.currentUser = currentUser;
    }

    @PostMapping("/api/claims")
    public ResponseEntity<ClaimResponse> create(@Valid @RequestBody ClaimRequest request) {
        currentUser.requireUser(request.claimantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(claimService.create(request));
    }

    @GetMapping("/api/claims/{id}")
    public ClaimResponse findById(@PathVariable Long id) {
        return claimService.findById(id);
    }

    @GetMapping("/api/items/{itemId}/claims")
    public List<ClaimResponse> findByItemId(@PathVariable Long itemId) {
        return claimService.findByItemId(itemId);
    }

    @GetMapping("/api/users/{userId}/claims")
    public List<ClaimResponse> findByClaimantId(@PathVariable Long userId) {
        currentUser.requireUser(userId);
        return claimService.findByClaimantId(userId);
    }

    @PutMapping("/api/claims/{id}/status")
    public ClaimResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClaimStatusUpdateRequest request) {
        return claimService.updateStatus(id, request);
    }
}
