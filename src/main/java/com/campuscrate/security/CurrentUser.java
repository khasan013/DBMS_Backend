package com.campuscrate.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.campuscrate.repository.UserRepository;
import com.campuscrate.repository.FoodRepository;

@Component
public class CurrentUser {
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    public CurrentUser(UserRepository userRepository, FoodRepository foodRepository) { this.userRepository = userRepository; this.foodRepository = foodRepository; }
    public void requireUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
                || !String.valueOf(userId).equals(authentication.getName())) {
            throw new AccessDeniedException("User identity does not match the authenticated account");
        }
        if (userRepository.findById(userId).map(user -> user.isSuspended()).orElse(true)) {
            throw new AccessDeniedException("This account has been suspended");
        }
    }

    public void requireAdmin(Long adminId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                || !String.valueOf(adminId).equals(authentication.getName())) {
            throw new AccessDeniedException("Admin identity does not match the authenticated account");
        }
    }

    public Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            throw new AccessDeniedException("A signed-in user is required");
        }
        try {
            Long userId = Long.valueOf(authentication.getName());
            if (userRepository.findById(userId).map(user -> user.isSuspended()).orElse(true)) {
                throw new AccessDeniedException("This account has been suspended");
            }
            return userId;
        } catch (NumberFormatException exception) { throw new AccessDeniedException("Invalid authenticated user"); }
    }

    public void requireNonVendor(Long userId) {
        requireUser(userId);
        if (foodRepository.findVendorByUser(userId).isPresent()) {
            throw new AccessDeniedException("Vendor accounts can post food items only.");
        }
    }
}
