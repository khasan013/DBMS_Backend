package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.Item;

public class ItemRowMapper implements RowMapper<Item> {

    @Override
    public Item mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Item(
                resultSet.getLong("item_id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("item_type"),
                resultSet.getString("image_url"),
                resultSet.getString("status"),
                resultSet.getObject("created_at", LocalDate.class),
                resultSet.getLong("reported_by"),
                resultSet.getLong("category_id"),
                resultSet.getLong("location_id"));
    }
}