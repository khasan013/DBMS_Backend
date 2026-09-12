package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.Category;

public class CategoryRowMapper implements RowMapper<Category> {

    @Override
    public Category mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Category(
                resultSet.getLong("category_id"),
                resultSet.getString("name"));
    }
}