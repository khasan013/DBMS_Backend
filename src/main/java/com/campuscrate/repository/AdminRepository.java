package com.campuscrate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.AdminRowMapper;
import com.campuscrate.model.Admin;

@Repository
public class AdminRepository {

    private static final String SELECT_COLUMNS = "admin_id, name, email, password_hash, phone, profile_image_url";

    private final JdbcTemplate jdbcTemplate;
    private final AdminRowMapper adminRowMapper = new AdminRowMapper();

    public AdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Admin> findById(Long adminId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `ADMIN` WHERE admin_id = ?";
        return findOne(sql, adminId);
    }

    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `ADMIN` WHERE LOWER(email) = LOWER(?)";
        return findOne(sql, email);
    }

    public boolean updateProfile(Long adminId, String email, String phone, String profileImageUrl) {
        String sql = "UPDATE `ADMIN` SET email = ?, phone = ?, profile_image_url = ? WHERE admin_id = ?";
        return jdbcTemplate.update(sql, email, phone, profileImageUrl, adminId) > 0;
    }

    public void upsertDefault(String name, String email, String passwordHash, String phone) {
        String sql = "INSERT INTO `ADMIN` (name, email, password_hash, phone) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name = VALUES(name), password_hash = VALUES(password_hash), phone = VALUES(phone)";
        jdbcTemplate.update(sql, name, email, passwordHash, phone);
    }

    private Optional<Admin> findOne(String sql, Object parameter) {
        List<Admin> admins = jdbcTemplate.query(sql, adminRowMapper, parameter);
        return admins.stream().findFirst();
    }
}
