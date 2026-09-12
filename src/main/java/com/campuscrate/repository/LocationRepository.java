package com.campuscrate.repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.LocationRowMapper;
import com.campuscrate.model.Location;

@Repository
public class LocationRepository {

    private static final String SELECT_COLUMNS = "location_id, name";

    private final JdbcTemplate jdbcTemplate;
    private final LocationRowMapper locationRowMapper = new LocationRowMapper();

    public LocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Location> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `LOCATION` ORDER BY location_id";
        return jdbcTemplate.query(sql, locationRowMapper);
    }

    public Optional<Location> findById(Long locationId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `LOCATION` WHERE location_id = ?";
        List<Location> locations = jdbcTemplate.query(sql, locationRowMapper, locationId);
        return locations.stream().findFirst();
    }

    public Location create(Location location) {
        String sql = "INSERT INTO `LOCATION` (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, location.getName());
            return preparedStatement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            location.setLocationId(generatedId.longValue());
        }
        return location;
    }

    public boolean update(Long locationId, String name) {
        String sql = "UPDATE `LOCATION` SET name = ? WHERE location_id = ?";
        return jdbcTemplate.update(sql, name, locationId) > 0;
    }

    public boolean delete(Long locationId) {
        String sql = "DELETE FROM `LOCATION` WHERE location_id = ?";
        return jdbcTemplate.update(sql, locationId) > 0;
    }

    public boolean existsById(Long locationId) {
        String sql = "SELECT COUNT(*) FROM `LOCATION` WHERE location_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, locationId);
        return count != null && count > 0;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM `LOCATION` WHERE LOWER(name) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    public boolean existsByNameExceptId(String name, Long locationId) {
        String sql = "SELECT COUNT(*) FROM `LOCATION` "
                + "WHERE LOWER(name) = LOWER(?) AND location_id <> ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name, locationId);
        return count != null && count > 0;
    }
}