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

    private static final String SELECT_COLUMNS = "h.history_id, c.item_id, h.claim_id, h.status";

    private final JdbcTemplate jdbcTemplate;
    private final StatusHistoryRowMapper statusHistoryRowMapper = new StatusHistoryRowMapper();

    public StatusHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StatusHistory create(StatusHistory statusHistory) {
        String sql = "INSERT INTO `CLAIM_STATUS_HISTORY` (claim_id, status) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, statusHistory.getClaimId());
            preparedStatement.setString(2, statusHistory.getStatus());
            return preparedStatement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            statusHistory.setHistoryId(generatedId.longValue());
        }
        return statusHistory;
    }

    public List<StatusHistory> findByItemId(Long itemId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CLAIM_STATUS_HISTORY` h "
                + "JOIN `CLAIM` c ON c.claim_id = h.claim_id WHERE c.item_id = ? ORDER BY h.history_id";
        return jdbcTemplate.query(sql, statusHistoryRowMapper, itemId);
    }

    public List<StatusHistory> findByClaimId(Long claimId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CLAIM_STATUS_HISTORY` h "
                + "JOIN `CLAIM` c ON c.claim_id = h.claim_id WHERE h.claim_id = ? ORDER BY h.history_id";
        return jdbcTemplate.query(sql, statusHistoryRowMapper, claimId);
    }
}
