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
        sendOtp(email, "email_verification_otp", "Verify your Campus Crate email", "verification");
    }

    public void sendPasswordResetOtp(String email) {
        sendOtp(email, "password_reset_otp", "Reset your Campus Crate password", "password reset");
    }

    /** Sends the administrator-created vendor their one-time login details. */
    public void sendVendorWelcome(String email, String vendorName, String loginId, String temporaryPassword,
            String phone, String location) {
        if (resendApiKey.isBlank() || fromEmail.isBlank()) throw new InvalidRequestException("Email delivery is not configured. Set RESEND_API_KEY and RESEND_FROM_EMAIL.");
        String html = "<h2>Welcome to Campus Crate Food</h2>"
                + "<p>Hello " + escape(vendorName) + ", your vendor account is ready.</p>"
                + "<p><strong>Login ID:</strong> " + escape(loginId) + "<br>"
                + "<strong>Temporary password:</strong> " + escape(temporaryPassword) + "<br>"
                + "<strong>Store location:</strong> " + escape(location) + "<br>"
                + "<strong>Phone:</strong> " + escape(phone) + "</p>"
                + "<p>Sign in to Campus Crate, then open <strong>Vendor dashboard</strong> to manage your store, menu, and orders. Please change your password after your first login.</p>";
        sendEmail(email, "Your Campus Crate vendor account", html, "Could not send vendor invitation email.");
    }

    private void sendOtp(String email, String table, String subject, String purpose) {
        if (resendApiKey.isBlank() || fromEmail.isBlank()) throw new InvalidRequestException("Email delivery is not configured. Set RESEND_API_KEY and RESEND_FROM_EMAIL.");
        String code = "%06d".formatted(random.nextInt(1_000_000));
        jdbcTemplate.update("INSERT INTO " + table + " (email, code_hash, expires_at, attempts) VALUES (?, ?, ?, 0) "
                + "ON DUPLICATE KEY UPDATE code_hash = VALUES(code_hash), expires_at = VALUES(expires_at), attempts = 0", email,
                passwordEncoder.encode(code), LocalDateTime.now().plusMinutes(10));
        sendEmail(email, subject, "<p>Your Campus Crate " + purpose + " code is <strong>" + code + "</strong>.</p><p>It expires in 10 minutes.</p>", "Could not send verification email.");
    }

    private void sendEmail(String email, String subject, String html, String failureMessage) {
        try {
            String body = objectMapper.writeValueAsString(java.util.Map.of("from", fromEmail, "to", java.util.List.of(email), "subject", subject, "html", html));
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) throw new InvalidRequestException(failureMessage);
        } catch (InvalidRequestException e) { throw e; } catch (Exception e) { throw new InvalidRequestException(failureMessage); }
    }

    private String escape(String value) { return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;"); }

    public void verify(String email, String code) {
        verifyOtp(email, code, "email_verification_otp");
        // Keep the table name's case consistent with the schema. This is required
        // by case-sensitive MySQL hosts such as Render's Linux environment.
        jdbcTemplate.update("UPDATE `USER` SET email_verified = TRUE WHERE email = ?", email);
    }

    public void verifyPasswordResetOtp(String email, String code) {
        verifyOtp(email, code, "password_reset_otp");
    }

    private void verifyOtp(String email, String code, String table) {
        var rows = jdbcTemplate.query("SELECT code_hash, expires_at, attempts FROM " + table + " WHERE email = ?",
                (rs, i) -> new Object[] { rs.getString(1), rs.getObject(2, LocalDateTime.class), rs.getInt(3) }, email);
        if (rows.isEmpty()) throw new InvalidRequestException("Request a new verification code.");
        Object[] otp = rows.getFirst();
        LocalDateTime expiresAt = (LocalDateTime) otp[1];
        if (expiresAt == null || (int) otp[2] >= 5 || LocalDateTime.now().isAfter(expiresAt)
                || !passwordEncoder.matches(code, (String) otp[0])) {
            jdbcTemplate.update("UPDATE " + table + " SET attempts = attempts + 1 WHERE email = ?", email);
            throw new InvalidRequestException("Invalid or expired verification code.");
        }
        jdbcTemplate.update("DELETE FROM " + table + " WHERE email = ?", email);
    }
}
