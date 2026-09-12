package com.campuscrate.repository;

import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.StatusHistoryRowMapper;
import com.campuscrate.model.StatusHistory;

@Repository
public class StatusHistoryRepository {

    private static final String SELECT_COLUMNS = "history_id, item_id, claim_id, status";

    private final JdbcTemplate jdbcTemplate;
    private final StatusHistoryRowMapper statusHistoryRowMapper = new StatusHistoryRowMapper();

    public StatusHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StatusHistory create(StatusHistory statusHistory) {
        String sql = "INSERT INTO `STATUS_HISTORY` (item_id, claim_id, status) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, statusHistory.getItemId());
            preparedStatement.setLong(2, statusHistory.getClaimId());
            preparedStatement.setString(3, statusHistory.getStatus());
            return preparedStatement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            statusHistory.setHistoryId(generatedId.longValue());
        }
        return statusHistory;
    }

    public List<StatusHistory> findByItemId(Long itemId) {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM `STATUS_HISTORY` WHERE item_id = ? ORDER BY history_id";
        return jdbcTemplate.query(sql, statusHistoryRowMapper, itemId);
    }

    public List<StatusHistory> findByClaimId(Long claimId) {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM `STATUS_HISTORY` WHERE claim_id = ? ORDER BY history_id";
        return jdbcTemplate.query(sql, statusHistoryRowMapper, claimId);
    }
}