package com.campuscrate.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Creates the application's baseline schema when it is connected to a new
 * database. Every statement is idempotent, so existing databases and data are
 * left untouched.
 */
@Component
@Order(0)
public class CoreSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public CoreSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `USER` ("
                + "user_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "student_id VARCHAR(64) NOT NULL, "
                + "name VARCHAR(120) NOT NULL, "
                + "email VARCHAR(255) NULL, "
                + "email_verified BOOLEAN NOT NULL DEFAULT FALSE, "
                + "password_hash VARCHAR(255) NOT NULL, "
                + "phone VARCHAR(32) NOT NULL, "
                + "profile_img_url VARCHAR(500) NULL, "
                + "UNIQUE KEY uk_user_student_id (student_id), "
                + "UNIQUE KEY uk_user_email (email))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `ADMIN` ("
                + "admin_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "email VARCHAR(255) NOT NULL, "
                + "password_hash VARCHAR(255) NOT NULL, "
                + "phone VARCHAR(32) NULL, "
                + "profile_image_url VARCHAR(500) NULL, "
                + "UNIQUE KEY uk_admin_email (email))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `CATEGORY` ("
                + "category_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "UNIQUE KEY uk_category_name (name))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `LOCATION` ("
                + "location_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(150) NOT NULL, "
                + "UNIQUE KEY uk_location_name (name))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `ITEM` ("
                + "item_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "title VARCHAR(255) NOT NULL, "
                + "description TEXT NOT NULL, "
                + "item_type VARCHAR(30) NOT NULL, "
                + "image_url VARCHAR(500) NULL, "
                + "status VARCHAR(30) NOT NULL, "
                + "created_at DATE NOT NULL DEFAULT (CURRENT_DATE), "
                + "reported_by BIGINT NOT NULL, "
                + "category_id BIGINT NOT NULL, "
                + "location_id BIGINT NOT NULL, "
                + "INDEX idx_item_reported_by (reported_by), "
                + "INDEX idx_item_category_id (category_id), "
                + "INDEX idx_item_location_id (location_id))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `CLAIM` ("
                + "claim_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "item_id BIGINT NOT NULL, "
                + "claimant_id BIGINT NOT NULL, "
                + "evidence_description TEXT NOT NULL, "
                + "evidence_img_url VARCHAR(500) NULL, "
                + "status VARCHAR(30) NOT NULL, "
                + "admin_id BIGINT NULL, "
                + "updated_at DATE NOT NULL DEFAULT (CURRENT_DATE), "
                + "INDEX idx_claim_item_id (item_id), "
                + "INDEX idx_claim_claimant_id (claimant_id), "
                + "INDEX idx_claim_admin_id (admin_id))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `STATUS_HISTORY` ("
                + "history_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "item_id BIGINT NOT NULL, "
                + "claim_id BIGINT NOT NULL, "
                + "status VARCHAR(30) NOT NULL, "
                + "INDEX idx_status_history_item_id (item_id), "
                + "INDEX idx_status_history_claim_id (claim_id))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `MARKETPLACE_POST` ("
                + "post_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "seller_id BIGINT NOT NULL, "
                + "category_id BIGINT NOT NULL, "
                + "location_id BIGINT NOT NULL, "
                + "title VARCHAR(255) NOT NULL, "
                + "description TEXT NOT NULL, "
                + "`condition` VARCHAR(50) NOT NULL, "
                + "selling_type VARCHAR(30) NOT NULL, "
                + "fixed_price DECIMAL(12,2) NULL, "
                + "starting_price DECIMAL(12,2) NULL, "
                + "auction_start DATETIME NULL, "
                + "auction_end DATETIME NULL, "
                + "status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', "
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "INDEX idx_marketplace_seller_id (seller_id), "
                + "INDEX idx_marketplace_category_id (category_id), "
                + "INDEX idx_marketplace_location_id (location_id))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `MARKETPLACE_SALE` ("
                + "sale_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "post_id BIGINT NOT NULL, "
                + "buyer_id BIGINT NOT NULL, "
                + "sale_price DECIMAL(12,2) NOT NULL, "
                + "status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', "
                + "sold_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "INDEX idx_marketplace_sale_post_id (post_id), "
                + "INDEX idx_marketplace_sale_buyer_id (buyer_id))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS shuttle_driver ("
                + "driver_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id BIGINT NOT NULL, "
                + "status VARCHAR(20) NOT NULL DEFAULT 'pending', "
                + "phone VARCHAR(32) NULL, vehicle_name VARCHAR(80) NULL, vehicle_number VARCHAR(40) NULL, "
                + "profile_info VARCHAR(240) NULL, approved_by BIGINT NULL, approved_at DATETIME NULL, "
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "UNIQUE KEY uk_shuttle_driver_user_id (user_id), "
                + "INDEX idx_shuttle_driver_status (status))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS shuttle_trip ("
                + "trip_id BIGINT AUTO_INCREMENT PRIMARY KEY, driver_id BIGINT NOT NULL, route VARCHAR(50) NOT NULL, "
                + "status VARCHAR(20) NOT NULL DEFAULT 'scheduled', scheduled_start_at DATETIME NOT NULL, "
                + "started_at DATETIME NULL, ended_at DATETIME NULL, latitude DECIMAL(10,7) NULL, "
                + "longitude DECIMAL(10,7) NULL, last_location_at DATETIME NULL, "
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "INDEX idx_shuttle_trip_driver_status (driver_id, status), "
                + "INDEX idx_shuttle_trip_route_status (route, status))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS shuttle_wait_request ("
                + "wait_request_id BIGINT AUTO_INCREMENT PRIMARY KEY, trip_id BIGINT NOT NULL, "
                + "driver_id BIGINT NOT NULL, user_id BIGINT NOT NULL, latitude DECIMAL(10,7) NOT NULL, "
                + "longitude DECIMAL(10,7) NOT NULL, status VARCHAR(30) NOT NULL DEFAULT 'waiting', "
                + "decision_at DATETIME NULL, inside_at DATETIME NULL, "
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "INDEX idx_shuttle_wait_trip_user_status (trip_id, user_id, status), "
                + "INDEX idx_shuttle_wait_driver_status (driver_id, status))");
    }
}
