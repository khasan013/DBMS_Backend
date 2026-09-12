package com.campuscrate.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.AdminLoginRequest;
import com.campuscrate.dto.AdminResponse;
import com.campuscrate.dto.AdminUpdateRequest;
import com.campuscrate.dto.AuthResponse;
import com.campuscrate.dto.ClaimResponse;
import com.campuscrate.exception.AdminNotFoundException;
import com.campuscrate.exception.DuplicateAdminException;
import com.campuscrate.exception.InvalidCredentialsException;
import com.campuscrate.exception.InvalidRequestException;
import com.campuscrate.model.Admin;
import com.campuscrate.repository.AdminRepository;
import com.campuscrate.security.JwtService;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final ClaimService claimService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminService(AdminRepository adminRepository, ClaimService claimService,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.claimService = claimService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse<AdminResponse> login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthResponse<>(jwtService.createToken(admin.getAdminId(), "ADMIN"), "Bearer",
                jwtService.expirationSeconds(), toResponse(admin));
    }

    public AdminResponse findById(Long adminId) {
        return toResponse(findAdmin(adminId));
    }

    @Transactional
    public AdminResponse updateProfile(Long adminId, AdminUpdateRequest request) {
        Admin existingAdmin = findAdmin(adminId);
        if (request.email() == null && request.phone() == null && request.profileImageUrl() == null) {
            throw new InvalidRequestException("At least one profile field is required");
        }

        String email = request.email() != null ? request.email() : existingAdmin.getEmail();
        String phone = request.phone() != null ? request.phone() : existingAdmin.getPhone();
        String profileImageUrl = request.profileImageUrl() != null
                ? request.profileImageUrl()
                : existingAdmin.getProfileImageUrl();

        try {
            adminRepository.updateProfile(adminId, email, phone, profileImageUrl);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateAdminException("email, phone, or profile image URL");
        }

        existingAdmin.setEmail(email);
        existingAdmin.setPhone(phone);
        existingAdmin.setProfileImageUrl(profileImageUrl);
        return toResponse(existingAdmin);
    }

    public List<ClaimResponse> findClaims() {
        return claimService.findAll();
    }

    public ClaimResponse updateClaimStatus(Long claimId,
            com.campuscrate.dto.ClaimStatusUpdateRequest request) {
        return claimService.updateStatus(claimId, request);
    }

    private Admin findAdmin(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException(adminId));
    }

    private AdminResponse toResponse(Admin admin) {
        return new AdminResponse(admin.getAdminId(), admin.getEmail(), admin.getPhone(),
                admin.getProfileImageUrl());
    }
}
