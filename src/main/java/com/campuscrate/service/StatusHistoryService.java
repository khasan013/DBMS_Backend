package com.campuscrate.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.StatusHistoryRequest;
import com.campuscrate.dto.StatusHistoryResponse;
import com.campuscrate.exception.StatusHistoryReferenceException;
import com.campuscrate.model.Claim;
import com.campuscrate.model.StatusHistory;
import com.campuscrate.repository.ClaimRepository;
import com.campuscrate.repository.ItemRepository;
import com.campuscrate.repository.StatusHistoryRepository;

@Service
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;
    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;

    public StatusHistoryService(StatusHistoryRepository statusHistoryRepository,
            ItemRepository itemRepository, ClaimRepository claimRepository) {
        this.statusHistoryRepository = statusHistoryRepository;
        this.itemRepository = itemRepository;
        this.claimRepository = claimRepository;
    }

    @Transactional
    public StatusHistoryResponse create(StatusHistoryRequest request) {
        Claim claim = claimRepository.findById(request.claimId())
                .orElseThrow(() -> new StatusHistoryReferenceException("claim", request.claimId()));
        StatusHistory statusHistory = new StatusHistory(
                null, claim.getItemId(), request.claimId(), request.status());
        return toResponse(statusHistoryRepository.create(statusHistory));
    }

    public List<StatusHistoryResponse> findByItemId(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new StatusHistoryReferenceException("item", itemId);
        }
        return statusHistoryRepository.findByItemId(itemId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StatusHistoryResponse> findByClaimId(Long claimId) {
        if (claimRepository.findById(claimId).isEmpty()) {
            throw new StatusHistoryReferenceException("claim", claimId);
        }
        return statusHistoryRepository.findByClaimId(claimId).stream()
                .map(this::toResponse)
                .toList();
    }

    private StatusHistoryResponse toResponse(StatusHistory statusHistory) {
        return new StatusHistoryResponse(statusHistory.getHistoryId(), statusHistory.getItemId(),
                statusHistory.getClaimId(), statusHistory.getStatus());
    }
}
