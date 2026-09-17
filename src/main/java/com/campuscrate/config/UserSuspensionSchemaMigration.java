package com.campuscrate.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class UserSuspensionSchemaMigration implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;
    public UserSuspensionSchemaMigration(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
    @Override public void run(ApplicationArguments args) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'USER' AND column_name = 'suspended'", Integer.class);
        if (count == null || count == 0) jdbcTemplate.execute("ALTER TABLE `USER` ADD COLUMN suspended BOOLEAN NOT NULL DEFAULT FALSE");
    }
}
