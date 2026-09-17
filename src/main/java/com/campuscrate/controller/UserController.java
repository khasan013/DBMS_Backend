package com.campuscrate.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.campuscrate.dto.UserLoginRequest;
import com.campuscrate.dto.UserRegistrationRequest;
import com.campuscrate.dto.UserResponse;
import com.campuscrate.dto.UserUpdateRequest;
import com.campuscrate.dto.PublicContactResponse;
import com.campuscrate.dto.AuthResponse;
import com.campuscrate.dto.EmailVerificationRequest;
import com.campuscrate.dto.ResendOtpRequest;
import com.campuscrate.service.UserService;
import com.campuscrate.security.CurrentUser;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CurrentUser currentUser;

    public UserController(UserService userService, CurrentUser currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse<UserResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return userService.login(request);
    }
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) { userService.verifyEmail(request.email(), request.code()); return ResponseEntity.noContent().build(); }
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendOtpRequest request) { userService.resendEmailOtp(request.email()); return ResponseEntity.noContent().build(); }

    @GetMapping("/{id}")
    public UserResponse getProfile(@PathVariable Long id) {
        currentUser.requireUser(id);
        return userService.getProfile(id);
    }

    @GetMapping("/{id}/contact")
    public PublicContactResponse getPublicContact(@PathVariable Long id) {
        return userService.getPublicContact(id);
    }

    @PutMapping("/{id}")
    public UserResponse updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        currentUser.requireUser(id);
        return userService.updateProfile(id, request);
    }
}
