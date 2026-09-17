package com.campuscrate.repository;

import java.math.BigDecimal;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.ToLetListingRowMapper;
import com.campuscrate.model.ToLetListing;

@Repository
public class ToLetListingRepository {
    private static final String COLUMNS = "listing_id, owner_id, title, description, area, monthly_rent, bedrooms, "
            + "bathrooms, contact_phone, available_from, status, created_at, updated_at";
    private final JdbcTemplate jdbcTemplate;
    private final ToLetListingRowMapper rowMapper = new ToLetListingRowMapper();

    public ToLetListingRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public ToLetListing create(ToLetListing listing) {
        String sql = "INSERT INTO to_let_listing (owner_id, title, description, area, monthly_rent, bedrooms, bathrooms, "
                + "contact_phone, available_from, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keys = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, listing.ownerId()); statement.setString(2, listing.title());
            statement.setString(3, listing.description()); statement.setString(4, listing.area());
            statement.setBigDecimal(5, listing.monthlyRent()); statement.setInt(6, listing.bedrooms());
            statement.setInt(7, listing.bathrooms()); statement.setString(8, listing.contactPhone());
            statement.setObject(9, listing.availableFrom()); statement.setString(10, listing.status());
            return statement;
        }, keys);
        Number key = keys.getKey();
        return new ToLetListing(key == null ? null : key.longValue(), listing.ownerId(), listing.title(), listing.description(),
                listing.area(), listing.monthlyRent(), listing.bedrooms(), listing.bathrooms(), listing.contactPhone(),
                listing.availableFrom(), listing.status(), null, null);
    }

    public Optional<ToLetListing> findById(Long listingId) {
        return jdbcTemplate.query("SELECT " + COLUMNS + " FROM to_let_listing WHERE listing_id = ?", rowMapper, listingId)
                .stream().findFirst();
    }

    public List<ToLetListing> findAll(String search, String area, BigDecimal maxRent, boolean availableOnly, Long ownerId) {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUMNS).append(" FROM to_let_listing WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();
        if (availableOnly) { sql.append(" AND status = 'AVAILABLE'"); }
        if (ownerId != null) { sql.append(" AND owner_id = ?"); parameters.add(ownerId); }
        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim() + "%";
            sql.append(" AND (LOWER(title) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?) OR LOWER(area) LIKE LOWER(?))");
            parameters.add(pattern); parameters.add(pattern); parameters.add(pattern);
        }
        if (area != null && !area.isBlank()) { sql.append(" AND LOWER(area) LIKE LOWER(?)"); parameters.add("%" + area.trim() + "%"); }
        if (maxRent != null) { sql.append(" AND monthly_rent <= ?"); parameters.add(maxRent); }
        sql.append(" ORDER BY listing_id DESC");
        return jdbcTemplate.query(sql.toString(), rowMapper, parameters.toArray());
    }

    public boolean update(ToLetListing listing) {
        return jdbcTemplate.update("UPDATE to_let_listing SET title = ?, description = ?, area = ?, monthly_rent = ?, bedrooms = ?, "
                + "bathrooms = ?, contact_phone = ?, available_from = ? WHERE listing_id = ?", listing.title(), listing.description(),
                listing.area(), listing.monthlyRent(), listing.bedrooms(), listing.bathrooms(), listing.contactPhone(),
                listing.availableFrom(), listing.listingId()) > 0;
    }

    public boolean close(Long listingId) {
        return jdbcTemplate.update("UPDATE to_let_listing SET status = 'CLOSED' WHERE listing_id = ?", listingId) > 0;
    }

    public boolean updateStatus(Long listingId, String status) {
        return jdbcTemplate.update("UPDATE to_let_listing SET status = ? WHERE listing_id = ?", status, listingId) > 0;
    }
}
