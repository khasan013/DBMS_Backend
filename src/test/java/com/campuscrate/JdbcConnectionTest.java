package com.campuscrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = "app.jwt.secret=test-only-jwt-secret-must-be-at-least-32-characters")
class JdbcConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void connectsToMySql() {
        Boolean connectionIsValid = jdbcTemplate.execute(
                (Connection connection) -> connection.isValid(5));

        assertTrue(connectionIsValid);
    }

    @Test
    void containsTheBackendSchema() {
        Set<String> expected = Set.of("user", "admin", "item", "category", "location", "claim",
                "claim_status_history", "marketplace_post", "marketplace_sale", "to_let_listing",
                "to_let_listing_photo");
        Set<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()", String.class)
                .stream().map(String::toLowerCase).collect(Collectors.toSet());
        Set<String> missing = expected.stream().filter(table -> !tables.contains(table)).collect(Collectors.toSet());
        assertEquals(Set.of(), missing, "Database is missing required tables: " + missing);
    }
}
