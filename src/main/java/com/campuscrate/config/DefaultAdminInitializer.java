package com.campuscrate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.campuscrate.repository.AdminRepository;

@Component
@Order(2)
public class DefaultAdminInitializer implements ApplicationRunner {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final String name;
    private final String email;
    private final String password;
    private final String phone;

    public DefaultAdminInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
            @Value("${DEFAULT_ADMIN_NAME:}") String name, @Value("${DEFAULT_ADMIN_EMAIL:}") String email,
            @Value("${DEFAULT_ADMIN_PASSWORD:}") String password, @Value("${DEFAULT_ADMIN_PHONE:}") String phone) {
        this.adminRepository = adminRepository; this.passwordEncoder = passwordEncoder;
        this.name = name; this.email = email; this.password = password; this.phone = phone;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) return;
        adminRepository.upsertDefault(name.trim(), email.trim(), passwordEncoder.encode(password), phone.trim());
    }
}
