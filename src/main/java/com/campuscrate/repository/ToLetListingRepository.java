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
    private static final String COLUMNS = "l.listing_id, l.owner_id, l.title, l.description, l.area, l.monthly_rent, l.bedrooms, "
            + "l.bathrooms, l.contact_phone, l.available_from, l.status, l.created_at, l.updated_at, "
            + "GROUP_CONCAT(p.photo_url ORDER BY p.display_order SEPARATOR '\\n') AS photo_urls";
    private static final String FROM_LISTINGS = " FROM to_let_listing l LEFT JOIN to_let_listing_photo p ON p.listing_id = l.listing_id ";
    private static final String GROUP_BY_LISTING = " GROUP BY l.listing_id, l.owner_id, l.title, l.description, l.area, l.monthly_rent, "
            + "l.bedrooms, l.bathrooms, l.contact_phone, l.available_from, l.status, l.created_at, l.updated_at";
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
        Long listingId = key == null ? null : key.longValue();
        if (listingId != null) replacePhotos(listingId, listing.photoUrls());
        return new ToLetListing(listingId, listing.ownerId(), listing.title(), listing.description(),
                listing.area(), listing.monthlyRent(), listing.bedrooms(), listing.bathrooms(), listing.contactPhone(),
                listing.availableFrom(), listing.status(), null, null, listing.photoUrls());
    }

    public Optional<ToLetListing> findById(Long listingId) {
        return jdbcTemplate.query("SELECT " + COLUMNS + FROM_LISTINGS + "WHERE l.listing_id = ?" + GROUP_BY_LISTING, rowMapper, listingId)
                .stream().findFirst();
    }

    public List<ToLetListing> findAll(String search, String area, BigDecimal maxRent, boolean availableOnly, Long ownerId) {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUMNS).append(FROM_LISTINGS).append("WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();
        if (availableOnly) { sql.append(" AND l.status = 'AVAILABLE'"); }
        if (ownerId != null) { sql.append(" AND l.owner_id = ?"); parameters.add(ownerId); }
        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim() + "%";
            sql.append(" AND (LOWER(l.title) LIKE LOWER(?) OR LOWER(l.description) LIKE LOWER(?) OR LOWER(l.area) LIKE LOWER(?))");
            parameters.add(pattern); parameters.add(pattern); parameters.add(pattern);
        }
        if (area != null && !area.isBlank()) { sql.append(" AND LOWER(l.area) LIKE LOWER(?)"); parameters.add("%" + area.trim() + "%"); }
        if (maxRent != null) { sql.append(" AND l.monthly_rent <= ?"); parameters.add(maxRent); }
        sql.append(GROUP_BY_LISTING).append(" ORDER BY l.listing_id DESC");
        return jdbcTemplate.query(sql.toString(), rowMapper, parameters.toArray());
    }

    public boolean update(ToLetListing listing) {
        boolean updated = jdbcTemplate.update("UPDATE to_let_listing SET title = ?, description = ?, area = ?, monthly_rent = ?, bedrooms = ?, "
                + "bathrooms = ?, contact_phone = ?, available_from = ? WHERE listing_id = ?", listing.title(), listing.description(),
                listing.area(), listing.monthlyRent(), listing.bedrooms(), listing.bathrooms(), listing.contactPhone(),
                listing.availableFrom(), listing.listingId()) > 0;
        if (updated) replacePhotos(listing.listingId(), listing.photoUrls());
        return updated;
    }

    public boolean close(Long listingId) {
        return jdbcTemplate.update("UPDATE to_let_listing SET status = 'CLOSED' WHERE listing_id = ?", listingId) > 0;
    }

    public boolean updateStatus(Long listingId, String status) {
        return jdbcTemplate.update("UPDATE to_let_listing SET status = ? WHERE listing_id = ?", status, listingId) > 0;
    }

    private void replacePhotos(Long listingId, List<String> photoUrls) {
        jdbcTemplate.update("DELETE FROM to_let_listing_photo WHERE listing_id = ?", listingId);
        for (int index = 0; index < photoUrls.size(); index++) {
            jdbcTemplate.update("INSERT INTO to_let_listing_photo (listing_id, photo_url, display_order) VALUES (?, ?, ?)", listingId, photoUrls.get(index), index + 1);
        }
    }
}
