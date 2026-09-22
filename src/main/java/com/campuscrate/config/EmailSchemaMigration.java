package com.campuscrate.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Applies the additive email-verification schema migration on startup. */
@Component
@Order(1)
public class EmailSchemaMigration implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public EmailSchemaMigration(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public void run(ApplicationArguments args) {
        if (!columnExists("email")) jdbcTemplate.execute("ALTER TABLE `USER` ADD COLUMN email VARCHAR(255) NULL");
        if (!columnExists("email_verified")) jdbcTemplate.execute("ALTER TABLE `USER` ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE");
        if (!indexExists("uk_user_email")) jdbcTemplate.execute("CREATE UNIQUE INDEX uk_user_email ON `USER` (email)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS email_verification_otp (email VARCHAR(255) PRIMARY KEY, code_hash VARCHAR(255) NOT NULL, expires_at DATETIME NOT NULL, attempts INT NOT NULL DEFAULT 0, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS password_reset_otp (email VARCHAR(255) PRIMARY KEY, code_hash VARCHAR(255) NOT NULL, expires_at DATETIME NOT NULL, attempts INT NOT NULL DEFAULT 0, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)");
    }

    private boolean columnExists(String column) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'USER' AND column_name = ?", Integer.class, column);
        return count != null && count > 0;
    }
    private boolean indexExists(String index) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'USER' AND index_name = ?", Integer.class, index);
        return count != null && count > 0;
    }
}
