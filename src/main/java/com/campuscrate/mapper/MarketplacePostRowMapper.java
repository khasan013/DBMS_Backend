package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.MarketplacePost;

public class MarketplacePostRowMapper implements RowMapper<MarketplacePost> {

    @Override
    public MarketplacePost mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new MarketplacePost(
                resultSet.getLong("post_id"),
                resultSet.getLong("seller_id"),
                resultSet.getLong("category_id"),
                resultSet.getLong("location_id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("condition"),
                resultSet.getString("selling_type"),
                resultSet.getBigDecimal("fixed_price"),
                resultSet.getBigDecimal("starting_price"),
                resultSet.getObject("auction_start", LocalDateTime.class),
                resultSet.getObject("auction_end", LocalDateTime.class),
                resultSet.getString("status"),
                resultSet.getObject("created_at", LocalDateTime.class),
                resultSet.getObject("updated_at", LocalDateTime.class));
    }
}