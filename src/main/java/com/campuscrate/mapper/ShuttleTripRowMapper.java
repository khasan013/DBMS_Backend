package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.ShuttleTrip;

public class ShuttleTripRowMapper implements RowMapper<ShuttleTrip> {

    @Override
    public ShuttleTrip mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new ShuttleTrip(
                resultSet.getLong("trip_id"),
                resultSet.getLong("driver_id"),
                resultSet.getString("route"),
                resultSet.getString("status"),
                resultSet.getObject("scheduled_start_at", LocalDateTime.class),
                resultSet.getObject("started_at", LocalDateTime.class),
                resultSet.getObject("ended_at", LocalDateTime.class),
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude"),
                resultSet.getObject("last_location_at", LocalDateTime.class),
                resultSet.getObject("created_at", LocalDateTime.class),
                resultSet.getObject("updated_at", LocalDateTime.class));
    }
}