package com.campuscrate.repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.CategoryRowMapper;
import com.campuscrate.model.Category;

@Repository
public class CategoryRepository {

    private static final String SELECT_COLUMNS = "category_id, name";

    private final JdbcTemplate jdbcTemplate;
    private final CategoryRowMapper categoryRowMapper = new CategoryRowMapper();

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CATEGORY` ORDER BY category_id";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public Optional<Category> findById(Long categoryId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CATEGORY` WHERE category_id = ?";
        List<Category> categories = jdbcTemplate.query(sql, categoryRowMapper, categoryId);
        return categories.stream().findFirst();
    }

    public Category create(Category category) {
        String sql = "INSERT INTO `CATEGORY` (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, category.getName());
            return preparedStatement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            category.setCategoryId(generatedId.longValue());
        }
        return category;
    }

    public boolean update(Long categoryId, String name) {
        String sql = "UPDATE `CATEGORY` SET name = ? WHERE category_id = ?";
        return jdbcTemplate.update(sql, name, categoryId) > 0;
    }

    public boolean delete(Long categoryId) {
        String sql = "DELETE FROM `CATEGORY` WHERE category_id = ?";
        return jdbcTemplate.update(sql, categoryId) > 0;
    }

    public boolean existsById(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM `CATEGORY` WHERE category_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, categoryId);
        return count != null && count > 0;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM `CATEGORY` WHERE LOWER(name) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    public boolean existsByNameExceptId(String name, Long categoryId) {
        String sql = "SELECT COUNT(*) FROM `CATEGORY` "
                + "WHERE LOWER(name) = LOWER(?) AND category_id <> ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name, categoryId);
        return count != null && count > 0;
    }
}