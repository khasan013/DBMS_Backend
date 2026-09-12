package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.ShuttleDriver;

public class ShuttleDriverRowMapper implements RowMapper<ShuttleDriver> {

    @Override
    public ShuttleDriver mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new ShuttleDriver(
                resultSet.getLong("driver_id"),
                resultSet.getLong("user_id"),
                resultSet.getString("status"),
                resultSet.getString("phone"),
                resultSet.getString("vehicle_name"),
                resultSet.getString("vehicle_number"),
                resultSet.getString("profile_info"),
                resultSet.getObject("approved_by", Long.class),
                resultSet.getObject("approved_at", LocalDateTime.class),
                resultSet.getObject("created_at", LocalDateTime.class),
                resultSet.getObject("updated_at", LocalDateTime.class));
    }
}