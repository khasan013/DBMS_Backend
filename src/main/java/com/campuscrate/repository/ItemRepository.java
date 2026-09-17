package com.campuscrate.repository;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.ItemRowMapper;
import com.campuscrate.model.Item;

@Repository
public class ItemRepository {

    private static final String SELECT_COLUMNS = "item_id, title, description, item_type, image_url, "
            + "status, created_at, reported_by, category_id, location_id";

    private final JdbcTemplate jdbcTemplate;
    private final ItemRowMapper itemRowMapper = new ItemRowMapper();

    public ItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Item> findAll(String search, String status, Long categoryId,
            Long locationId, String itemType) {
        StringBuilder sql = new StringBuilder("SELECT ")
                .append(SELECT_COLUMNS)
                .append(" FROM `ITEM` WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (LOWER(title) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?))");
            String searchPattern = "%" + search.trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            parameters.add(status.trim());
        }
        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            parameters.add(categoryId);
        }
        if (locationId != null) {
            sql.append(" AND location_id = ?");
            parameters.add(locationId);
        }
        if (itemType != null && !itemType.isBlank()) {
            sql.append(" AND item_type = ?");
            parameters.add(itemType.trim());
        }
        sql.append(" ORDER BY item_id");

        return jdbcTemplate.query(sql.toString(), itemRowMapper, parameters.toArray());
    }

    public Optional<Item> findById(Long itemId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `ITEM` WHERE item_id = ?";
        List<Item> items = jdbcTemplate.query(sql, itemRowMapper, itemId);
        return items.stream().findFirst();
    }

    public Item create(Item item) {
        String sql = "INSERT INTO `ITEM` "
                + "(title, description, item_type, image_url, status, reported_by, category_id, location_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.setString(3, item.getItemType());
            preparedStatement.setString(4, item.getImageUrl());
            preparedStatement.setString(5, item.getStatus());
            preparedStatement.setLong(6, item.getReportedBy());
            preparedStatement.setLong(7, item.getCategoryId());
            preparedStatement.setLong(8, item.getLocationId());
            return preparedStatement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            item.setItemId(generatedId.longValue());
        }
        return item;
    }

    public boolean update(Long itemId, Item item) {
        String sql = "UPDATE `ITEM` SET title = ?, description = ?, item_type = ?, image_url = ?, "
                + "status = ?, reported_by = ?, category_id = ?, location_id = ? WHERE item_id = ?";
        return jdbcTemplate.update(sql, item.getTitle(), item.getDescription(), item.getItemType(),
                item.getImageUrl(), item.getStatus(), item.getReportedBy(), item.getCategoryId(),
                item.getLocationId(), itemId) > 0;
    }

    public boolean delete(Long itemId) {
        String sql = "DELETE FROM `ITEM` WHERE item_id = ?";
        return jdbcTemplate.update(sql, itemId) > 0;
    }

    public boolean updateStatus(Long itemId, String status) {
        return jdbcTemplate.update("UPDATE `ITEM` SET status = ? WHERE item_id = ?", status, itemId) > 0;
    }
}
