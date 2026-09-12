package com.campuscrate.repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.campuscrate.mapper.ClaimRowMapper;
import com.campuscrate.model.Claim;

@Repository
public class ClaimRepository {

    private static final String SELECT_COLUMNS = "claim_id, item_id, claimant_id, evidence_description, "
            + "evidence_img_url, status, admin_id, updated_at";

    private final JdbcTemplate jdbcTemplate;
    private final ClaimRowMapper claimRowMapper = new ClaimRowMapper();

    public ClaimRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Claim create(Claim claim) {
        String sql = "INSERT INTO `CLAIM` "
                + "(item_id, claimant_id, evidence_description, evidence_img_url, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, claim.getItemId());
            preparedStatement.setLong(2, claim.getClaimantId());
            preparedStatement.setString(3, claim.getEvidenceDescription());
            preparedStatement.setString(4, claim.getEvidenceImgUrl());
            preparedStatement.setString(5, claim.getStatus());
            return preparedStatement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            claim.setClaimId(generatedId.longValue());
        }
        return claim;
    }

    public Optional<Claim> findById(Long claimId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CLAIM` WHERE claim_id = ?";
        return findOne(sql, claimId);
    }

    public List<Claim> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CLAIM` ORDER BY claim_id";
        return jdbcTemplate.query(sql, claimRowMapper);
    }

    public List<Claim> findByItemId(Long itemId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CLAIM` WHERE item_id = ? ORDER BY claim_id";
        return jdbcTemplate.query(sql, claimRowMapper, itemId);
    }

    public List<Claim> findByClaimantId(Long claimantId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM `CLAIM` WHERE claimant_id = ? ORDER BY claim_id";
        return jdbcTemplate.query(sql, claimRowMapper, claimantId);
    }

    public boolean updateStatus(Long claimId, String status, Long adminId) {
        String sql = "UPDATE `CLAIM` SET status = ?, admin_id = ?, updated_at = CURRENT_DATE "
                + "WHERE claim_id = ?";
        return jdbcTemplate.update(sql, status, adminId, claimId) > 0;
    }

    public boolean existsByItemId(Long itemId) {
        String sql = "SELECT COUNT(*) FROM `CLAIM` WHERE item_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, itemId);
        return count != null && count > 0;
    }

    public boolean existsAdminById(Long adminId) {
        String sql = "SELECT COUNT(*) FROM `ADMIN` WHERE admin_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, adminId);
        return count != null && count > 0;
    }

    private Optional<Claim> findOne(String sql, Object parameter) {
        List<Claim> claims = jdbcTemplate.query(sql, claimRowMapper, parameter);
        return claims.stream().findFirst();
    }
}