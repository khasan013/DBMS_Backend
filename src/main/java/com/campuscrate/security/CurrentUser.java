package com.campuscrate.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public void requireUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
                || !String.valueOf(userId).equals(authentication.getName())) {
            throw new AccessDeniedException("User identity does not match the authenticated account");
        }
    }
}
