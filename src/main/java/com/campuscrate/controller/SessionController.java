package com.campuscrate.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.repository.AdminRepository;
import com.campuscrate.repository.UserRepository;

@RestController
@RequestMapping("/api/session")
public class SessionController {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public SessionController(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @GetMapping
    public void validate() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) throw new AccessDeniedException("Session is invalid");
        Long accountId;
        try { accountId = Long.valueOf(authentication.getName()); }
        catch (NumberFormatException exception) { throw new AccessDeniedException("Session is invalid"); }
        boolean isUser = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        boolean active = isUser
                ? userRepository.findById(accountId).map(user -> !user.isSuspended()).orElse(false)
                : adminRepository.findById(accountId).isPresent();
        if (!active) throw new AccessDeniedException("This account is no longer active");
    }
}
