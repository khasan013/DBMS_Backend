package com.campuscrate.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.campuscrate.dto.UserLoginRequest;
import com.campuscrate.dto.UserRegistrationRequest;
import com.campuscrate.dto.UserResponse;
import com.campuscrate.dto.UserUpdateRequest;
import com.campuscrate.dto.AuthResponse;
import com.campuscrate.exception.DuplicateStudentIdException;
import com.campuscrate.exception.InvalidCredentialsException;
import com.campuscrate.exception.InvalidRequestException;
import com.campuscrate.exception.UserNotFoundException;
import com.campuscrate.model.User;
import com.campuscrate.repository.UserRepository;
import com.campuscrate.security.JwtService;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByStudentId(request.studentId())) {
            throw new DuplicateStudentIdException(request.studentId());
        }
        if (userRepository.existsByEmail(request.email())) throw new InvalidRequestException("An account already uses this email.");

        User user = new User(
                null,
                request.studentId(),
                request.name(),
                request.email(), false,
                passwordEncoder.encode(request.password()),
                request.phone(),
                request.profileImgUrl());
        try {
            UserResponse response = toResponse(userRepository.create(user));
            emailVerificationService.sendOtp(request.email());
            return response;
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateStudentIdException(request.studentId());
        }
    }

    public AuthResponse<UserResponse> login(UserLoginRequest request) {
        User user = userRepository.findByStudentId(request.studentId())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEmailVerified()) throw new InvalidRequestException("Verify your email before signing in.");
        return new AuthResponse<>(jwtService.createToken(user.getUserId(), "USER"), "Bearer",
                jwtService.expirationSeconds(), toResponse(user));
    }
    public void verifyEmail(String email, String code) { emailVerificationService.verify(email, code); }
    public void resendEmailOtp(String email) { if (userRepository.findByEmail(email).isEmpty()) throw new InvalidRequestException("No account exists for this email."); emailVerificationService.sendOtp(email); }

    public UserResponse getProfile(Long userId) {
        return toResponse(findUser(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User existingUser = findUser(userId);
        if (request.name() == null && request.phone() == null && request.profileImgUrl() == null) {
            throw new InvalidRequestException("At least one profile field is required");
        }

        String name = request.name() != null ? request.name() : existingUser.getName();
        String phone = request.phone() != null ? request.phone() : existingUser.getPhone();
        String profileImgUrl = request.profileImgUrl() != null
                ? request.profileImgUrl()
                : existingUser.getProfileImgUrl();

        userRepository.updateProfile(userId, name, phone, profileImgUrl);
        existingUser.setName(name);
        existingUser.setPhone(phone);
        existingUser.setProfileImgUrl(profileImgUrl);
        return toResponse(existingUser);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getStudentId(),
                user.getName(),
                user.getEmail(), user.isEmailVerified(),
                user.getPhone(),
                user.getProfileImgUrl());
    }
}
