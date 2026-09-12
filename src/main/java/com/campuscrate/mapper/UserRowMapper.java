package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.User;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getLong("user_id"),
                resultSet.getString("student_id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getBoolean("email_verified"),
                resultSet.getString("password_hash"),
                resultSet.getString("phone"),
                resultSet.getString("profile_img_url"));
    }
}
