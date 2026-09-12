package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.ShuttleWaitRequest;

public class ShuttleWaitRequestRowMapper implements RowMapper<ShuttleWaitRequest> {

    @Override
    public ShuttleWaitRequest mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new ShuttleWaitRequest(
                resultSet.getLong("wait_request_id"),
                resultSet.getLong("trip_id"),
                resultSet.getLong("driver_id"),
                resultSet.getLong("user_id"),
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude"),
                resultSet.getString("status"),
                resultSet.getObject("decision_at", LocalDateTime.class),
                resultSet.getObject("inside_at", LocalDateTime.class),
                resultSet.getObject("created_at", LocalDateTime.class),
                resultSet.getObject("updated_at", LocalDateTime.class));
    }
}