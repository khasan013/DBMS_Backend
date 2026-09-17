package com.campuscrate.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AdminSchemaMigration implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public AdminSchemaMigration(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public void run(ApplicationArguments args) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'ADMIN' AND column_name = 'name'", Integer.class);
        if (count == null || count == 0) jdbcTemplate.execute("ALTER TABLE `ADMIN` ADD COLUMN name VARCHAR(120) NOT NULL DEFAULT 'Administrator' AFTER admin_id");
    }
}
