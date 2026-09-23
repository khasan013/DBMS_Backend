package com.campuscrate.repository;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.MarketplacePostRowMapper;
import com.campuscrate.model.MarketplacePost;

@Repository
public class MarketplacePostRepository {

    private static final String SELECT_COLUMNS = "post_id, seller_id, category_id, location_id, title, description, "
            + "`condition`, selling_type, fixed_price, starting_price, auction_start, auction_end, status, "
            + "created_at, updated_at";

    private final JdbcTemplate jdbcTemplate;
    private final MarketplacePostRowMapper rowMapper = new MarketplacePostRowMapper();

    public MarketplacePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MarketplacePost create(MarketplacePost post) {
        String sql = "INSERT INTO `MARKETPLACE_POST` "
                + "(seller_id, category_id, location_id, title, description, `condition`, selling_type, "
                + "fixed_price, starting_price, auction_start, auction_end, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, post.getSellerId());
            statement.setLong(2, post.getCategoryId());
            statement.setLong(3, post.getLocationId());
            statement.setString(4, post.getTitle());
            statement.setString(5, post.getDescription());
            statement.setString(6, post.getCondition());
            statement.setString(7, post.getSellingType());
            statement.setBigDecimal(8, post.getFixedPrice());
            statement.setBigDecimal(9, post.getStartingPrice());
            statement.setObject(10, post.getAuctionStart());
            statement.setObject(11, post.getAuctionEnd());
            statement.setString(12, post.getStatus());
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            post.setPostId(generatedId.longValue());
        }
        return post;
    }

    public Optional<MarketplacePost> findById(Long postId) {
        return findOne("SELECT " + SELECT_COLUMNS + " FROM `MARKETPLACE_POST` WHERE post_id = ?", postId);
    }

    public List<MarketplacePost> findAll(String search, Long categoryId, Long locationId,
            String sellingType, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            boolean activeOnly, Long sellerId) {
        StringBuilder sql = new StringBuilder("SELECT ").append(SELECT_COLUMNS)
                .append(" FROM `MARKETPLACE_POST` WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();

        if (activeOnly) { sql.append(" AND status = ?"); parameters.add("ACTIVE"); }
        if (sellerId != null) { sql.append(" AND seller_id = ?"); parameters.add(sellerId); }
        if (search != null && !search.isBlank()) {
            sql.append(" AND (LOWER(title) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?))");
            String pattern = "%" + search.trim() + "%";
            parameters.add(pattern);
            parameters.add(pattern);
        }
        if (categoryId != null) { sql.append(" AND category_id = ?"); parameters.add(categoryId); }
        if (locationId != null) { sql.append(" AND location_id = ?"); parameters.add(locationId); }
        if (sellingType != null && !sellingType.isBlank()) {
            sql.append(" AND selling_type = ?"); parameters.add(sellingType.trim());
        }
        if (minPrice != null) {
            sql.append(" AND COALESCE(fixed_price, starting_price) >= ?"); parameters.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND COALESCE(fixed_price, starting_price) <= ?"); parameters.add(maxPrice);
        }
        sql.append(" ORDER BY post_id DESC");
        return jdbcTemplate.query(sql.toString(), rowMapper, parameters.toArray());
    }

    public boolean update(Long postId, MarketplacePost post) {
        String sql = "UPDATE `MARKETPLACE_POST` SET category_id = ?, location_id = ?, title = ?, description = ?, "
                + "`condition` = ?, selling_type = ?, fixed_price = ?, starting_price = ?, auction_start = ?, "
                + "auction_end = ? WHERE post_id = ?";
        return jdbcTemplate.update(sql, post.getCategoryId(), post.getLocationId(), post.getTitle(),
                post.getDescription(), post.getCondition(), post.getSellingType(), post.getFixedPrice(),
                post.getStartingPrice(), post.getAuctionStart(), post.getAuctionEnd(), postId) > 0;
    }

    public boolean cancel(Long postId) {
        return jdbcTemplate.update("UPDATE `MARKETPLACE_POST` SET status = 'CANCELLED' WHERE post_id = ?",
                postId) > 0;
    }

    public boolean deleteWithSales(Long postId) {
        jdbcTemplate.update("DELETE FROM `MARKETPLACE_SALE` WHERE post_id = ?", postId);
        return jdbcTemplate.update("DELETE FROM `MARKETPLACE_POST` WHERE post_id = ?", postId) > 0;
    }

    public boolean updateStatus(Long postId, String status) {
        return jdbcTemplate.update("UPDATE `MARKETPLACE_POST` SET status = ? WHERE post_id = ?",
                status, postId) > 0;
    }

    public boolean existsById(Long postId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM `MARKETPLACE_POST` WHERE post_id = ?", Integer.class, postId);
        return count != null && count > 0;
    }

    private Optional<MarketplacePost> findOne(String sql, Object parameter) {
        List<MarketplacePost> posts = jdbcTemplate.query(sql, rowMapper, parameter);
        return posts.stream().findFirst();
    }
}
