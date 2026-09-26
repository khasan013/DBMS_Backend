package com.campuscrate.repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.UserRowMapper;
import com.campuscrate.model.User;

@Repository
public class UserRepository {

    private static final String SELECT_COLUMNS = "user_id, student_id, name, email, email_verified, suspended, password_hash, phone, profile_img_url";

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User create(User user) {
        String sql = "INSERT INTO `USER` (student_id, name, email, email_verified, password_hash, phone, profile_img_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getStudentId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getEmail()); preparedStatement.setBoolean(4, user.isEmailVerified());
            preparedStatement.setString(5, user.getPasswordHash()); preparedStatement.setString(6, user.getPhone()); preparedStatement.setString(7, user.getProfileImgUrl());
            return preparedStatement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            user.setUserId(generatedId.longValue());
        }
        return user;
    }

    public Optional<User> findById(Long userId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `USER` WHERE user_id = ?";
        return findOne(sql, userId);
    }

    public Optional<User> findByStudentId(String studentId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `USER` WHERE student_id = ?";
        return findOne(sql, studentId);
    }

    public boolean existsByStudentId(String studentId) {
        String sql = "SELECT COUNT(*) FROM `USER` WHERE student_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null && count > 0;
    }
    public boolean existsByEmail(String email) { Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `USER` WHERE email = ?", Integer.class, email); return count != null && count > 0; }
    public Optional<User> findByEmail(String email) { return findOne("SELECT " + SELECT_COLUMNS + " FROM `USER` WHERE email = ?", email); }

    public boolean updateProfile(Long userId, String name, String phone, String profileImgUrl) {
        String sql = "UPDATE `USER` SET name = ?, phone = ?, profile_img_url = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, name, phone, profileImgUrl, userId) > 0;
    }

    public boolean updatePassword(Long userId, String passwordHash) {
        return jdbcTemplate.update("UPDATE `USER` SET password_hash = ? WHERE user_id = ?", passwordHash, userId) > 0;
    }

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM `USER` ORDER BY user_id DESC", userRowMapper);
    }

    public boolean setSuspended(Long userId, boolean suspended) {
        return jdbcTemplate.update("UPDATE `USER` SET suspended = ? WHERE user_id = ?", suspended, userId) > 0;
    }

    public boolean deleteWithRelatedData(User user) {
        Long userId = user.getUserId();
        jdbcTemplate.update("DELETE oi FROM food_order_item oi JOIN food_order o ON o.food_order_id = oi.food_order_id "
                + "WHERE o.buyer_id = ? OR o.vendor_id IN (SELECT vendor_id FROM food_vendor WHERE user_id = ?)", userId, userId);
        jdbcTemplate.update("DELETE FROM food_order WHERE buyer_id = ? OR vendor_id IN (SELECT vendor_id FROM food_vendor WHERE user_id = ?)", userId, userId);
        jdbcTemplate.update("DELETE FROM food_item WHERE vendor_id IN (SELECT vendor_id FROM food_vendor WHERE user_id = ?)", userId);
        jdbcTemplate.update("DELETE FROM food_vendor WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE h FROM `CLAIM_STATUS_HISTORY` h JOIN `CLAIM` c ON c.claim_id = h.claim_id "
                + "WHERE c.claimant_id = ? OR c.item_id IN (SELECT item_id FROM `ITEM` WHERE reported_by = ?)", userId, userId);
        jdbcTemplate.update("DELETE FROM `CLAIM` WHERE claimant_id = ? OR item_id IN (SELECT item_id FROM `ITEM` WHERE reported_by = ?)", userId, userId);
        jdbcTemplate.update("DELETE FROM `MARKETPLACE_SALE` WHERE buyer_id = ? OR post_id IN (SELECT post_id FROM `MARKETPLACE_POST` WHERE seller_id = ?)", userId, userId);
        jdbcTemplate.update("DELETE FROM `MARKETPLACE_POST` WHERE seller_id = ?", userId);
        jdbcTemplate.update("DELETE p FROM to_let_listing_photo p JOIN to_let_listing l ON l.listing_id = p.listing_id WHERE l.owner_id = ?", userId);
        jdbcTemplate.update("DELETE FROM to_let_listing WHERE owner_id = ?", userId);
        jdbcTemplate.update("DELETE FROM `ITEM` WHERE reported_by = ?", userId);
        jdbcTemplate.update("DELETE FROM email_verification_otp WHERE email = ?", user.getEmail());
        jdbcTemplate.update("DELETE FROM password_reset_otp WHERE email = ?", user.getEmail());
        return jdbcTemplate.update("DELETE FROM `USER` WHERE user_id = ?", userId) > 0;
    }

    private Optional<User> findOne(String sql, Object parameter) {
        List<User> users = jdbcTemplate.query(sql, userRowMapper, parameter);
        return users.stream().findFirst();
    }
}
