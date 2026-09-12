package com.campuscrate.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.ClaimRequest;
import com.campuscrate.dto.ClaimResponse;
import com.campuscrate.dto.ClaimStatusUpdateRequest;
import com.campuscrate.exception.ClaimNotFoundException;
import com.campuscrate.exception.ClaimReferenceNotFoundException;
import com.campuscrate.exception.DuplicateClaimException;
import com.campuscrate.model.Claim;
import com.campuscrate.repository.ClaimRepository;
import com.campuscrate.repository.ItemRepository;
import com.campuscrate.repository.UserRepository;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository,
            UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ClaimResponse create(ClaimRequest request) {
        validateItemAndClaimant(request.itemId(), request.claimantId());
        if (claimRepository.existsByItemId(request.itemId())) {
            throw new DuplicateClaimException(request.itemId());
        }

        Claim claim = new Claim(null, request.itemId(), request.claimantId(),
                request.evidenceDescription(), request.evidenceImgUrl(), request.status(), null, null);
        try {
            return toResponse(claimRepository.create(claim));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateClaimException(request.itemId());
        }
    }

    public ClaimResponse findById(Long claimId) {
        return toResponse(findClaim(claimId));
    }

    public List<ClaimResponse> findAll() {
        return claimRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ClaimResponse> findByItemId(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new ClaimReferenceNotFoundException("item", itemId);
        }
        return claimRepository.findByItemId(itemId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ClaimResponse> findByClaimantId(Long claimantId) {
        if (userRepository.findById(claimantId).isEmpty()) {
            throw new ClaimReferenceNotFoundException("claimant", claimantId);
        }
        return claimRepository.findByClaimantId(claimantId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClaimResponse updateStatus(Long claimId, ClaimStatusUpdateRequest request) {
        findClaim(claimId);
        if (!claimRepository.existsAdminById(request.adminId())) {
            throw new ClaimReferenceNotFoundException("admin", request.adminId());
        }

        try {
            claimRepository.updateStatus(claimId, request.status(), request.adminId());
        } catch (DataIntegrityViolationException exception) {
            throw new ClaimReferenceNotFoundException("admin", request.adminId());
        }
        return toResponse(findClaim(claimId));
    }

    private void validateItemAndClaimant(Long itemId, Long claimantId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new ClaimReferenceNotFoundException("item", itemId);
        }
        if (userRepository.findById(claimantId).isEmpty()) {
            throw new ClaimReferenceNotFoundException("claimant", claimantId);
        }
    }

    private Claim findClaim(Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException(claimId));
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(claim.getClaimId(), claim.getItemId(), claim.getClaimantId(),
                claim.getEvidenceDescription(), claim.getEvidenceImgUrl(), claim.getStatus(),
                claim.getAdminId(), claim.getUpdatedAt());
    }
}