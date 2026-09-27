package com.campuscrate.repository;

import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.campuscrate.model.MarketplaceSale;

@Repository
public class MarketplaceSaleRepository {
    private final JdbcTemplate jdbcTemplate;
    public MarketplaceSaleRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public MarketplaceSale create(Long postId, Long buyerId, java.math.BigDecimal price) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement("INSERT INTO `MARKETPLACE_SALE` (post_id, buyer_id, sale_price, status) VALUES (?, ?, ?, 'COMPLETED')", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, postId); statement.setLong(2, buyerId); statement.setBigDecimal(3, price);
            return statement;
        }, keys);
        return new MarketplaceSale(keys.getKey().longValue(), postId, buyerId, price, "COMPLETED", null);
    }
}
