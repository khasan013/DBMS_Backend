package com.campuscrate.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.AdminLoginRequest;
import com.campuscrate.dto.AdminResponse;
import com.campuscrate.dto.PublicAdminContactResponse;
import com.campuscrate.dto.AdminUpdateRequest;
import com.campuscrate.dto.AuthResponse;
import com.campuscrate.dto.ClaimResponse;
import com.campuscrate.dto.AdminUserResponse;
import com.campuscrate.exception.AdminNotFoundException;
import com.campuscrate.exception.DuplicateAdminException;
import com.campuscrate.exception.InvalidCredentialsException;
import com.campuscrate.exception.InvalidRequestException;
import com.campuscrate.model.Admin;
import com.campuscrate.repository.AdminRepository;
import com.campuscrate.repository.UserRepository;
import com.campuscrate.service.ItemService;
import com.campuscrate.service.MarketplacePostService;
import com.campuscrate.service.ToLetListingService;
import com.campuscrate.security.JwtService;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final ClaimService claimService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final MarketplacePostService marketplacePostService;
    private final ToLetListingService toLetListingService;

    public AdminService(AdminRepository adminRepository, ClaimService claimService,
            PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository userRepository,
            ItemService itemService, MarketplacePostService marketplacePostService, ToLetListingService toLetListingService) {
        this.adminRepository = adminRepository;
        this.claimService = claimService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.marketplacePostService = marketplacePostService;
        this.toLetListingService = toLetListingService;
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

    public PublicAdminContactResponse getPublicContact() {
        Admin admin = adminRepository.findPrimaryAdmin()
                .orElseThrow(() -> new AdminNotFoundException(0L));
        return new PublicAdminContactResponse(admin.getAdminId(), admin.getName(), admin.getPhone(), admin.getProfileImageUrl());
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

    public List<AdminUserResponse> findUsers() {
        return userRepository.findAll().stream().map(this::toAdminUserResponse).toList();
    }

    @Transactional
    public AdminUserResponse setUserSuspended(Long userId, boolean suspended) {
        AdminUserResponse user = userRepository.findById(userId).map(this::toAdminUserResponse)
                .orElseThrow(() -> new com.campuscrate.exception.UserNotFoundException(userId));
        userRepository.setSuspended(userId, suspended);
        return new AdminUserResponse(user.userId(), user.studentId(), user.name(), user.email(),
                user.emailVerified(), user.phone(), user.profileImgUrl(), suspended);
    }

    @Transactional
    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new com.campuscrate.exception.UserNotFoundException(userId));
        userRepository.deleteWithRelatedData(user);
    }

    public List<com.campuscrate.dto.ItemResponse> findItems() {
        return itemService.findAll(null, null, null, null, null);
    }

    public com.campuscrate.dto.ItemResponse updateItemStatus(Long itemId, String status) { return itemService.updateStatusByAdmin(itemId, status); }

    public List<com.campuscrate.dto.MarketplacePostResponse> findMarketplacePosts() {
        return marketplacePostService.findAll(null, null, null, null, null, null);
    }

    public com.campuscrate.dto.MarketplacePostResponse updateMarketplaceStatus(Long postId, String status) { return marketplacePostService.updateStatusByAdmin(postId, status); }
    public com.campuscrate.dto.ToLetListingResponse updateToLetStatus(Long listingId, String status) { return toLetListingService.updateStatusByAdmin(listingId, status); }
    public List<com.campuscrate.dto.ToLetListingResponse> findToLetListings() { return toLetListingService.findAllForAdmin(); }

    private Admin findAdmin(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException(adminId));
    }

    private AdminResponse toResponse(Admin admin) {
        return new AdminResponse(admin.getAdminId(), admin.getName(), admin.getEmail(), admin.getPhone(),
                admin.getProfileImageUrl());
    }

    private AdminUserResponse toAdminUserResponse(com.campuscrate.model.User user) {
        return new AdminUserResponse(user.getUserId(), user.getStudentId(), user.getName(), user.getEmail(),
                user.isEmailVerified(), user.getPhone(), user.getProfileImgUrl(), user.isSuspended());
    }
}
