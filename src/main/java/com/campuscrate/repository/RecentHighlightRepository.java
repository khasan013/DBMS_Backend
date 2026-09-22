package com.campuscrate.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.campuscrate.dto.RecentHighlightResponse;

@Repository
public class RecentHighlightRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecentHighlightRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RecentHighlightResponse> findRecent(int limit) {
        return jdbcTemplate.query("SELECT highlight_id, module, title, description, status, price, image_url, created_at, category_or_area "
                + "FROM recent_highlights ORDER BY created_at DESC LIMIT ?", (resultSet, row) -> new RecentHighlightResponse(
                        resultSet.getString("highlight_id"), resultSet.getString("module"), resultSet.getString("title"),
                        resultSet.getString("description"), resultSet.getString("status"), resultSet.getBigDecimal("price"),
                        resultSet.getString("image_url"), resultSet.getObject("created_at", java.time.LocalDateTime.class),
                        resultSet.getString("category_or_area")), limit);
    }
}
