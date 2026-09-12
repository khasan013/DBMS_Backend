package com.campuscrate.repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.ShuttleDriverRowMapper;
import com.campuscrate.model.ShuttleDriver;

@Repository
public class ShuttleDriverRepository {

    private static final String SELECT_COLUMNS = "driver_id, user_id, status, phone, vehicle_name, "
            + "vehicle_number, profile_info, approved_by, approved_at, created_at, updated_at";

    private final JdbcTemplate jdbcTemplate;
    private final ShuttleDriverRowMapper rowMapper = new ShuttleDriverRowMapper();

    public ShuttleDriverRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ShuttleDriver create(ShuttleDriver driver) {
        String sql = "INSERT INTO shuttle_driver (user_id, status, phone, vehicle_name, vehicle_number, profile_info) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, driver.getUserId());
            statement.setString(2, driver.getStatus());
            statement.setString(3, driver.getPhone());
            statement.setString(4, driver.getVehicleName());
            statement.setString(5, driver.getVehicleNumber());
            statement.setString(6, driver.getProfileInfo());
            return statement;
        }, keyHolder);
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            driver.setDriverId(generatedId.longValue());
        }
        return driver;
    }

    public Optional<ShuttleDriver> findById(Long driverId) {
        return findOne("SELECT " + SELECT_COLUMNS + " FROM shuttle_driver WHERE driver_id = ?", driverId);
    }

    public Optional<ShuttleDriver> findByUserId(Long userId) {
        return findOne("SELECT " + SELECT_COLUMNS + " FROM shuttle_driver WHERE user_id = ?", userId);
    }

    public List<ShuttleDriver> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM shuttle_driver ORDER BY driver_id", rowMapper);
    }

    public boolean updateStatus(Long userId, String status, Long approvedBy) {
        String sql = "UPDATE shuttle_driver SET status = ?, "
                + "approved_by = CASE WHEN ? = 'approved' THEN ? ELSE NULL END, "
                + "approved_at = CASE WHEN ? = 'approved' THEN CURRENT_TIMESTAMP ELSE NULL END "
                + "WHERE user_id = ? AND status = 'pending'";
        return jdbcTemplate.update(sql, status, status, approvedBy, status, userId) > 0;
    }

    public boolean existsByUserId(Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM shuttle_driver WHERE user_id = ?", Integer.class, userId);
        return count != null && count > 0;
    }

    private Optional<ShuttleDriver> findOne(String sql, Object parameter) {
        return jdbcTemplate.query(sql, rowMapper, parameter).stream().findFirst();
    }
}