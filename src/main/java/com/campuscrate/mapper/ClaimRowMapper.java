package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.Claim;

public class ClaimRowMapper implements RowMapper<Claim> {

    @Override
    public Claim mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Claim(
                resultSet.getLong("claim_id"),
                resultSet.getLong("item_id"),
                resultSet.getLong("claimant_id"),
                resultSet.getString("evidence_description"),
                resultSet.getString("evidence_img_url"),
                resultSet.getString("status"),
                resultSet.getObject("admin_id", Long.class),
                resultSet.getObject("updated_at", LocalDate.class));
    }
}