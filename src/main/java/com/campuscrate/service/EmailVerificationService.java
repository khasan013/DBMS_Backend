package com.campuscrate.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.campuscrate.exception.InvalidRequestException;

@Service
public class EmailVerificationService {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final String resendApiKey;
    private final String fromEmail;
    private final SecureRandom random = new SecureRandom();

    public EmailVerificationService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, ObjectMapper objectMapper,
            @Value("${resend.api-key:}") String resendApiKey,
            @Value("${resend.from-email:}") String fromEmail) {
        this.jdbcTemplate = jdbcTemplate; this.passwordEncoder = passwordEncoder; this.objectMapper = objectMapper;
        this.resendApiKey = resendApiKey; this.fromEmail = fromEmail;
    }

    public void sendOtp(String email) {
        if (resendApiKey.isBlank() || fromEmail.isBlank()) throw new InvalidRequestException("Email delivery is not configured. Set RESEND_API_KEY and RESEND_FROM_EMAIL.");
        String code = "%06d".formatted(random.nextInt(1_000_000));
        jdbcTemplate.update("INSERT INTO email_verification_otp (email, code_hash, expires_at, attempts) VALUES (?, ?, ?, 0) "
                + "ON DUPLICATE KEY UPDATE code_hash = VALUES(code_hash), expires_at = VALUES(expires_at), attempts = 0", email,
                passwordEncoder.encode(code), LocalDateTime.now().plusMinutes(10));
        try {
            String body = objectMapper.writeValueAsString(java.util.Map.of("from", fromEmail, "to", java.util.List.of(email),
                    "subject", "Verify your Campus Crate email", "html", "<p>Your Campus Crate verification code is <strong>" + code + "</strong>.</p><p>It expires in 10 minutes.</p>"));
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) throw new InvalidRequestException("Could not send verification email.");
        } catch (InvalidRequestException e) { throw e; } catch (Exception e) { throw new InvalidRequestException("Could not send verification email."); }
    }

    public void verify(String email, String code) {
        var rows = jdbcTemplate.query("SELECT code_hash, expires_at, attempts FROM email_verification_otp WHERE email = ?", (rs, i) -> new Object[] { rs.getString(1), rs.getTimestamp(2).toLocalDateTime(), rs.getInt(3) }, email);
        if (rows.isEmpty()) throw new InvalidRequestException("Request a new verification code.");
        Object[] otp = rows.getFirst();
        if ((int) otp[2] >= 5 || LocalDateTime.now().isAfter((LocalDateTime) otp[1]) || !passwordEncoder.matches(code, (String) otp[0])) {
            jdbcTemplate.update("UPDATE email_verification_otp SET attempts = attempts + 1 WHERE email = ?", email);
            throw new InvalidRequestException("Invalid or expired verification code.");
        }
        jdbcTemplate.update("UPDATE `user` SET email_verified = TRUE WHERE email = ?", email);
        jdbcTemplate.update("DELETE FROM email_verification_otp WHERE email = ?", email);
    }
}
