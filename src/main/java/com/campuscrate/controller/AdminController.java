package com.campuscrate.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.dto.AdminLoginRequest;
import com.campuscrate.dto.AdminResponse;
import com.campuscrate.dto.AdminUpdateRequest;
import com.campuscrate.dto.AuthResponse;
import com.campuscrate.dto.ClaimResponse;
import com.campuscrate.dto.ClaimStatusUpdateRequest;
import com.campuscrate.dto.AdminUserResponse;
import com.campuscrate.dto.UserSuspensionRequest;
import com.campuscrate.service.AdminService;
import com.campuscrate.security.CurrentUser;

@RestController
public class AdminController {

    private final AdminService adminService;
    private final CurrentUser currentUser;

    public AdminController(AdminService adminService, CurrentUser currentUser) {
        this.adminService = adminService;
        this.currentUser = currentUser;
    }

    @PostMapping("/api/admin/login")
    public AuthResponse<AdminResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return adminService.login(request);
    }

    @GetMapping("/api/admin/{id}")
    public AdminResponse findById(@PathVariable Long id) {
        currentUser.requireAdmin(id);
        return adminService.findById(id);
    }

    @PutMapping("/api/admin/{id}")
    public AdminResponse updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateRequest request) {
        currentUser.requireAdmin(id);
        return adminService.updateProfile(id, request);
    }

    @GetMapping("/api/admin/claims")
    public List<ClaimResponse> findClaims() {
        return adminService.findClaims();
    }

    @PutMapping("/api/admin/claims/{claimId}/status")
    public ClaimResponse updateClaimStatus(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimStatusUpdateRequest request) {
        return adminService.updateClaimStatus(claimId, request);
    }

    @GetMapping("/api/admin/users")
    public List<AdminUserResponse> findUsers() { return adminService.findUsers(); }

    @PutMapping("/api/admin/users/{userId}/suspension")
    public AdminUserResponse setUserSuspended(@PathVariable Long userId,
            @Valid @RequestBody UserSuspensionRequest request) {
        return adminService.setUserSuspended(userId, request.suspended());
    }
}
