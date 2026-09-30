package com.campuscrate.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
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
                + "suspended BOOLEAN NOT NULL DEFAULT FALSE, "
                + "password_hash VARCHAR(255) NOT NULL, "
                + "phone VARCHAR(32) NOT NULL, "
                + "profile_img_url VARCHAR(500) NULL, "
                + "UNIQUE KEY uk_user_student_id (student_id), "
                + "UNIQUE KEY uk_user_email (email))");
        jdbcTemplate.execute("UPDATE `USER` SET email_verified = TRUE WHERE email_verified = FALSE");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `ADMIN` ("
                + "admin_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(120) NOT NULL, "
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
                + "image_url VARCHAR(500) NULL, "
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

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `CLAIM_STATUS_HISTORY` ("
                + "history_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "claim_id BIGINT NOT NULL, "
                + "status VARCHAR(30) NOT NULL, "
                + "changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "INDEX idx_claim_history_changed (claim_id, changed_at DESC))");

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
                + "UNIQUE KEY uk_marketplace_sale_post (post_id), "
                + "INDEX idx_marketplace_sale_buyer_sold (buyer_id, sold_at DESC))");

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

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS to_let_listing_photo ("
                + "photo_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "listing_id BIGINT NOT NULL, "
                + "photo_url VARCHAR(500) NOT NULL, "
                + "display_order TINYINT UNSIGNED NOT NULL, "
                + "UNIQUE KEY uk_to_let_photo_order (listing_id, display_order), "
                + "CONSTRAINT fk_to_let_photo_listing FOREIGN KEY (listing_id) REFERENCES to_let_listing (listing_id) ON DELETE CASCADE)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS email_verification_otp ("
                + "email VARCHAR(255) PRIMARY KEY, code_hash VARCHAR(255) NOT NULL, expires_at DATETIME NOT NULL, "
                + "attempts INT NOT NULL DEFAULT 0, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS password_reset_otp ("
                + "email VARCHAR(255) PRIMARY KEY, code_hash VARCHAR(255) NOT NULL, expires_at DATETIME NOT NULL, "
                + "attempts INT NOT NULL DEFAULT 0, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS notification (notification_id BIGINT AUTO_INCREMENT PRIMARY KEY, recipient_type VARCHAR(12) NOT NULL, recipient_id BIGINT NULL, title VARCHAR(160) NOT NULL, message VARCHAR(1000) NOT NULL, is_read BOOLEAN NOT NULL DEFAULT FALSE, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, INDEX idx_notification_user_recent (recipient_type, recipient_id, notification_id DESC))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS food_vendor ("
                + "vendor_id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL, name VARCHAR(150) NOT NULL, "
                + "location VARCHAR(150) NOT NULL, description TEXT NULL, phone VARCHAR(32) NOT NULL, image_url VARCHAR(500) NULL, "
                + "active BOOLEAN NOT NULL DEFAULT TRUE, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "UNIQUE KEY uk_food_vendor_user (user_id), CONSTRAINT fk_food_vendor_user FOREIGN KEY (user_id) REFERENCES `USER` (user_id))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS food_item ("
                + "food_item_id BIGINT AUTO_INCREMENT PRIMARY KEY, vendor_id BIGINT NOT NULL, name VARCHAR(150) NOT NULL, "
                + "description TEXT NULL, price DECIMAL(12,2) NOT NULL, image_url VARCHAR(500) NULL, available BOOLEAN NOT NULL DEFAULT TRUE, "
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "INDEX idx_food_item_vendor_available (vendor_id, available), CONSTRAINT fk_food_item_vendor FOREIGN KEY (vendor_id) REFERENCES food_vendor (vendor_id) ON DELETE CASCADE)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS food_order ("
                + "food_order_id BIGINT AUTO_INCREMENT PRIMARY KEY, vendor_id BIGINT NOT NULL, buyer_id BIGINT NOT NULL, total_amount DECIMAL(12,2) NOT NULL, "
                + "payment_method VARCHAR(20) NOT NULL, payment_status VARCHAR(30) NOT NULL, order_status VARCHAR(30) NOT NULL, delivery_location VARCHAR(500) NOT NULL, transaction_id VARCHAR(64) NULL, "
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "UNIQUE KEY uk_food_order_transaction (transaction_id), INDEX idx_food_order_buyer (buyer_id), INDEX idx_food_order_vendor (vendor_id), "
                + "CONSTRAINT fk_food_order_vendor FOREIGN KEY (vendor_id) REFERENCES food_vendor (vendor_id), CONSTRAINT fk_food_order_buyer FOREIGN KEY (buyer_id) REFERENCES `USER` (user_id))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS food_order_item ("
                + "food_order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY, food_order_id BIGINT NOT NULL, food_item_id BIGINT NOT NULL, "
                + "quantity INT NOT NULL, unit_price DECIMAL(12,2) NOT NULL, CONSTRAINT fk_food_order_item_order FOREIGN KEY (food_order_id) REFERENCES food_order (food_order_id) ON DELETE CASCADE, "
                + "CONSTRAINT fk_food_order_item_food FOREIGN KEY (food_item_id) REFERENCES food_item (food_item_id))");
        ensureColumn("food_order", "delivery_location", "VARCHAR(500) NULL");
        ensureColumn("MARKETPLACE_POST", "image_url", "VARCHAR(500) NULL");
        jdbcTemplate.execute("CREATE OR REPLACE VIEW recent_highlights AS "
                + "SELECT CONCAT('lost-', i.item_id) AS highlight_id, 'lost' AS module, i.title, i.description, i.status, "
                + "NULL AS price, i.image_url, i.created_at AS created_at, c.name AS category_or_area "
                + "FROM `ITEM` i JOIN `CATEGORY` c ON c.category_id = i.category_id WHERE i.status IN ('LOST', 'FOUND') "
                + "UNION ALL "
                + "SELECT CONCAT('market-', p.post_id), 'market', p.title, p.description, p.status, COALESCE(p.fixed_price, p.starting_price), "
                + "p.image_url, p.created_at, c.name FROM `MARKETPLACE_POST` p JOIN `CATEGORY` c ON c.category_id = p.category_id WHERE p.status = 'ACTIVE' "
                + "UNION ALL "
                + "SELECT CONCAT('to-let-', l.listing_id), 'to-let', l.title, l.description, l.status, l.monthly_rent, "
                + "(SELECT ph.photo_url FROM to_let_listing_photo ph WHERE ph.listing_id = l.listing_id ORDER BY ph.display_order LIMIT 1), "
                + "l.created_at, l.area FROM to_let_listing l WHERE l.status = 'AVAILABLE'");
        createPerformanceIndexes();
        jdbcTemplate.execute("INSERT IGNORE INTO `CATEGORY` (name) VALUES ('General')");
        jdbcTemplate.execute("INSERT IGNORE INTO `LOCATION` (name) VALUES ('Campus')");
    }

    private void createPerformanceIndexes() {
        // No indexes duplicate a primary key, unique key, or an existing single-column relationship index.
        ensureIndex("MARKETPLACE_POST", "idx_marketplace_active_recent", "status, created_at DESC", false);
        ensureIndex("MARKETPLACE_POST", "idx_marketplace_status_category", "status, category_id", false);
        ensureIndex("ITEM", "idx_item_status_created", "status, created_at DESC", false);
        ensureIndex("CLAIM", "idx_claim_status_updated", "status, updated_at DESC", false);
        ensureIndex("CLAIM_STATUS_HISTORY", "idx_claim_history_changed", "claim_id, changed_at DESC", false);
        dropIndexIfExists("CLAIM_STATUS_HISTORY", "idx_claim_status_history_claim_id");
        ensureIndex("MARKETPLACE_SALE", "uk_marketplace_sale_post", "post_id", true);
        dropIndexIfExists("MARKETPLACE_SALE", "idx_marketplace_sale_post_id");
        ensureIndex("MARKETPLACE_SALE", "idx_marketplace_sale_buyer_sold", "buyer_id, sold_at DESC", false);
        dropIndexIfExists("MARKETPLACE_SALE", "idx_marketplace_sale_buyer_id");
        ensureIndex("to_let_listing", "idx_to_let_status_rent", "status, monthly_rent", false);
    }

    private void ensureIndex(String table, String index, String columns, boolean unique) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = ? AND index_name = ?", Integer.class, table, index);
        if (count == null || count == 0) jdbcTemplate.execute("CREATE " + (unique ? "UNIQUE " : "") + "INDEX " + index + " ON `" + table + "` (" + columns + ")");
    }

    private void dropIndexIfExists(String table, String index) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = ? AND index_name = ?", Integer.class, table, index);
        if (count != null && count > 0) jdbcTemplate.execute("DROP INDEX " + index + " ON `" + table + "`");
    }

    private void ensureColumn(String table, String column, String definition) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?", Integer.class, table, column);
        if (count == null || count == 0) jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
    }

}
