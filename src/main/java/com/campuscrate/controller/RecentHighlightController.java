package com.campuscrate.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.dto.RecentHighlightResponse;
import com.campuscrate.repository.RecentHighlightRepository;

@RestController
@RequestMapping("/api/highlights")
public class RecentHighlightController {
    private final RecentHighlightRepository recentHighlightRepository;

    public RecentHighlightController(RecentHighlightRepository recentHighlightRepository) {
        this.recentHighlightRepository = recentHighlightRepository;
    }

    @GetMapping("/recent")
    public List<RecentHighlightResponse> findRecent(@RequestParam(defaultValue = "12") int limit) {
        return recentHighlightRepository.findRecent(Math.clamp(limit, 1, 50));
    }
}
