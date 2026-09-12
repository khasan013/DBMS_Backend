package com.campuscrate.repository;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.ShuttleWaitRequestRowMapper;
import com.campuscrate.model.ShuttleWaitRequest;

@Repository
public class ShuttleWaitRequestRepository {

    private static final String SELECT_COLUMNS = "wait_request_id, trip_id, driver_id, user_id, latitude, longitude, "
            + "status, decision_at, inside_at, created_at, updated_at";
    private static final String OPEN_STATUSES = "('scheduled', 'active', 'in_progress')";

    private final JdbcTemplate jdbcTemplate;
    private final ShuttleWaitRequestRowMapper rowMapper = new ShuttleWaitRequestRowMapper();

    public ShuttleWaitRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ShuttleWaitRequest create(ShuttleWaitRequest request) {
        String sql = "INSERT INTO shuttle_wait_request (trip_id, driver_id, user_id, latitude, longitude) "
                + "SELECT ?, ?, ?, ?, ? FROM shuttle_trip "
                + "WHERE trip_id = ? AND driver_id = ? AND driver_id <> ? AND status IN " + OPEN_STATUSES;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, request.getTripId());
            statement.setLong(2, request.getDriverId());
            statement.setLong(3, request.getUserId());
            statement.setBigDecimal(4, request.getLatitude());
            statement.setBigDecimal(5, request.getLongitude());
            statement.setLong(6, request.getTripId());
            statement.setLong(7, request.getDriverId());
            statement.setLong(8, request.getUserId());
            return statement;
        }, keyHolder);
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            request.setWaitRequestId(generatedId.longValue());
        }
        return request;
    }

    public Optional<ShuttleWaitRequest> findById(Long waitRequestId) {
        return findOne("SELECT " + SELECT_COLUMNS + " FROM shuttle_wait_request WHERE wait_request_id = ?",
                waitRequestId);
    }

    public List<ShuttleWaitRequest> findByTripId(Long tripId) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS
                + " FROM shuttle_wait_request WHERE trip_id = ? ORDER BY created_at", rowMapper, tripId);
    }

    public List<ShuttleWaitRequest> findByDriverIdAndStatus(Long driverId, String status) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS
                + " FROM shuttle_wait_request WHERE driver_id = ? AND status = ? ORDER BY created_at",
                rowMapper, driverId, status);
    }

    public boolean updateStatus(Long waitRequestId, String status, LocalDateTime decisionAt,
            LocalDateTime insideAt) {
        String sql = "UPDATE shuttle_wait_request SET status = ?, decision_at = ?, inside_at = ? "
                + "WHERE wait_request_id = ?";
        return jdbcTemplate.update(sql, status, decisionAt, insideAt, waitRequestId) > 0;
    }

    public int cancelWaitingRequestsForTrip(Long tripId) {
        return jdbcTemplate.update("UPDATE shuttle_wait_request SET status = 'cancelled', decision_at = CURRENT_TIMESTAMP "
                + "WHERE trip_id = ? AND status = 'waiting'", tripId);
    }

    private Optional<ShuttleWaitRequest> findOne(String sql, Object parameter) {
        return jdbcTemplate.query(sql, rowMapper, parameter).stream().findFirst();
    }
}