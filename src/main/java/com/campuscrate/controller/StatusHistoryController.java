package com.campuscrate.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.dto.StatusHistoryResponse;
import com.campuscrate.service.StatusHistoryService;

@RestController
public class StatusHistoryController {

    private final StatusHistoryService statusHistoryService;

    public StatusHistoryController(StatusHistoryService statusHistoryService) {
        this.statusHistoryService = statusHistoryService;
    }

    @GetMapping("/api/items/{itemId}/status-history")
    public List<StatusHistoryResponse> findByItemId(@PathVariable Long itemId) {
        return statusHistoryService.findByItemId(itemId);
    }

    @GetMapping("/api/claims/{claimId}/status-history")
    public List<StatusHistoryResponse> findByClaimId(@PathVariable Long claimId) {
        return statusHistoryService.findByClaimId(claimId);
    }
}