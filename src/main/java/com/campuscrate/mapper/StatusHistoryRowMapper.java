package com.campuscrate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.campuscrate.model.StatusHistory;

public class StatusHistoryRowMapper implements RowMapper<StatusHistory> {

    @Override
    public StatusHistory mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new StatusHistory(
                resultSet.getLong("history_id"),
                resultSet.getLong("item_id"),
                resultSet.getLong("claim_id"),
                resultSet.getString("status"));
    }
}