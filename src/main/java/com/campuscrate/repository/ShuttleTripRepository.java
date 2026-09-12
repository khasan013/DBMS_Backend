package com.campuscrate.repository;

import java.sql.Statement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.ShuttleTripRowMapper;
import com.campuscrate.model.ShuttleTrip;

@Repository
public class ShuttleTripRepository {

    private static final String SELECT_COLUMNS = "trip_id, driver_id, route, status, scheduled_start_at, started_at, "
            + "ended_at, latitude, longitude, last_location_at, created_at, updated_at";
    private static final String OPEN_STATUSES = "('scheduled', 'active', 'in_progress')";

    private final JdbcTemplate jdbcTemplate;
    private final ShuttleTripRowMapper rowMapper = new ShuttleTripRowMapper();

    public ShuttleTripRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ShuttleTrip create(ShuttleTrip trip) {
        String sql = "INSERT INTO shuttle_trip (driver_id, route, status, scheduled_start_at, started_at, ended_at, "
                + "latitude, longitude, last_location_at) "
                + "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ? FROM shuttle_driver "
                + "WHERE user_id = ? AND status = 'approved' "
                + "AND NOT EXISTS (SELECT 1 FROM shuttle_trip WHERE driver_id = ? AND status IN " + OPEN_STATUSES + ")";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, trip.getDriverId());
            statement.setString(2, trip.getRoute());
            statement.setString(3, trip.getStatus());
            statement.setObject(4, trip.getScheduledStartAt());
            statement.setObject(5, trip.getStartedAt());
            statement.setObject(6, trip.getEndedAt());
            statement.setBigDecimal(7, trip.getLatitude());
            statement.setBigDecimal(8, trip.getLongitude());
            statement.setObject(9, trip.getLastLocationAt());
            statement.setLong(10, trip.getDriverId());
            statement.setLong(11, trip.getDriverId());
            return statement;
        }, keyHolder);
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            trip.setTripId(generatedId.longValue());
        }
        return trip;
    }

    public ShuttleTrip create(Long driverId, String route, boolean startNow) {
        LocalDateTime now = LocalDateTime.now();
        ShuttleTrip trip = new ShuttleTrip(
                null,
                driverId,
                route,
                startNow ? "in_progress" : "scheduled",
                startNow ? now : now.plusMinutes(5),
                startNow ? now : null,
                null,
                null,
                null,
                null,
                null,
                null);
        return create(trip);
    }

    public Optional<ShuttleTrip> findById(Long tripId) {
        return findOne("SELECT " + SELECT_COLUMNS + " FROM shuttle_trip WHERE trip_id = ?", tripId);
    }

    public List<ShuttleTrip> findByDriverId(Long driverId) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM shuttle_trip WHERE driver_id = ? ORDER BY trip_id",
                rowMapper, driverId);
    }

    public List<ShuttleTrip> findByRouteAndStatus(String route, String status) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS
                + " FROM shuttle_trip WHERE route = ? AND status = ? ORDER BY updated_at DESC", rowMapper, route, status);
    }

    public Optional<ShuttleTrip> findOpenByDriverId(Long driverId) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM shuttle_trip WHERE driver_id = ? AND status IN "
                + OPEN_STATUSES + " ORDER BY updated_at DESC LIMIT 1", rowMapper, driverId).stream().findFirst();
    }

    public boolean updateLocation(Long tripId, Long driverId, BigDecimal latitude, BigDecimal longitude,
            LocalDateTime lastLocationAt) {
        String sql = "UPDATE shuttle_trip SET latitude = ?, longitude = ?, last_location_at = ? "
                + "WHERE trip_id = ? AND driver_id = ? AND status IN " + OPEN_STATUSES;
        return jdbcTemplate.update(sql, latitude, longitude, lastLocationAt, tripId, driverId) > 0;
    }

    public boolean updateStatus(Long tripId, Long driverId, String status, LocalDateTime startedAt,
            LocalDateTime endedAt) {
        String sql = "UPDATE shuttle_trip SET status = ?, started_at = ?, ended_at = ? "
                + "WHERE trip_id = ? AND driver_id = ? AND status IN " + OPEN_STATUSES;
        return jdbcTemplate.update(sql, status, startedAt, endedAt, tripId, driverId) > 0;
    }

    public boolean cancelScheduledTrip(Long tripId, Long driverId) {
        String sql = "UPDATE shuttle_trip SET status = 'cancelled', ended_at = CURRENT_TIMESTAMP "
                + "WHERE trip_id = ? AND driver_id = ? AND status = 'scheduled'";
        return jdbcTemplate.update(sql, tripId, driverId) > 0;
    }

    private Optional<ShuttleTrip> findOne(String sql, Object parameter) {
        return jdbcTemplate.query(sql, rowMapper, parameter).stream().findFirst();
    }
}