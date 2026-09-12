package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.ToLetListing;

public class ToLetListingRowMapper implements RowMapper<ToLetListing> {
    @Override
    public ToLetListing mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new ToLetListing(resultSet.getLong("listing_id"), resultSet.getLong("owner_id"),
                resultSet.getString("title"), resultSet.getString("description"), resultSet.getString("area"),
                resultSet.getBigDecimal("monthly_rent"), resultSet.getInt("bedrooms"), resultSet.getInt("bathrooms"),
                resultSet.getString("contact_phone"), resultSet.getObject("available_from", LocalDate.class),
                resultSet.getString("status"), resultSet.getObject("created_at", LocalDateTime.class),
                resultSet.getObject("updated_at", LocalDateTime.class));
    }
}
