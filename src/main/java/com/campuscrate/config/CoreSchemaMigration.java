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

        // The old transport feature is retired. Drop child tables first so databases
        // created by previous versions are cleaned up safely.
        jdbcTemplate.execute("DROP TABLE IF EXISTS shuttle_wait_request");
        jdbcTemplate.execute("DROP TABLE IF EXISTS shuttle_trip");
        jdbcTemplate.execute("DROP TABLE IF EXISTS shuttle_driver");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS to_let_listing ("
                + "listing_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "owner_id BIGINT NOT NULL, "
                + "title VARCHAR(255) NOT NULL, "
                + "description TEXT NOT NULL, "
                + "area VARCHAR(150) NOT NULL, "
                + "monthly_rent DECIMAL(12,2) NOT NULL, "
                + "bedrooms INT NOT NULL, "
                + "bathrooms INT NOT NULL, "
                + "contact_phone VARCHAR(32) NOT NULL, "
                + "available_from DATE NULL, "
                + "status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', "
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "INDEX idx_to_let_owner_status (owner_id, status), "
                + "INDEX idx_to_let_area_status (area, status), "
                + "CONSTRAINT fk_to_let_owner FOREIGN KEY (owner_id) REFERENCES `USER` (user_id))");
    }
}
