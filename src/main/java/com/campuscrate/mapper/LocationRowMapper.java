package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.Location;

public class LocationRowMapper implements RowMapper<Location> {

    @Override
    public Location mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Location(
                resultSet.getLong("location_id"),
                resultSet.getString("name"));
    }
}