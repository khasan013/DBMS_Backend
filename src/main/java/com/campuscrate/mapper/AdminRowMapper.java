package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.Admin;

public class AdminRowMapper implements RowMapper<Admin> {

    @Override
    public Admin mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Admin(
                resultSet.getLong("admin_id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                resultSet.getString("phone"),
                resultSet.getString("profile_image_url"));
    }
}
