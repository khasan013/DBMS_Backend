package com.campuscrate.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.dto.MarketplacePostCreateRequest;
import com.campuscrate.dto.MarketplacePostResponse;
import com.campuscrate.dto.MarketplacePostUpdateRequest;
import com.campuscrate.service.MarketplacePostService;
import com.campuscrate.security.CurrentUser;

@RestController
public class MarketplacePostController {

    private final MarketplacePostService postService;
    private final CurrentUser currentUser;

    public MarketplacePostController(MarketplacePostService postService, CurrentUser currentUser) {
        this.postService = postService;
        this.currentUser = currentUser;
    }

    @PostMapping("/api/marketplace/posts")
    public ResponseEntity<MarketplacePostResponse> create(
            @Valid @RequestBody MarketplacePostCreateRequest request) {
        currentUser.requireNonVendor(request.sellerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(request));
    }

    @GetMapping("/api/marketplace/posts")
    public List<MarketplacePostResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String sellingType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return postService.findAll(search, categoryId, locationId, sellingType, minPrice, maxPrice);
    }

    @GetMapping("/api/marketplace/posts/active")
    public List<MarketplacePostResponse> findActive() {
        return postService.findActive();
    }

    @GetMapping("/api/marketplace/posts/{postId}")
    public MarketplacePostResponse findById(@PathVariable Long postId) {
        return postService.findById(postId);
    }

    @PutMapping("/api/marketplace/posts/{postId}")
    public MarketplacePostResponse update(
            @PathVariable Long postId,
            @RequestParam Long sellerId,
            @Valid @RequestBody MarketplacePostUpdateRequest request) {
        currentUser.requireNonVendor(sellerId);
        return postService.update(postId, sellerId, request);
    }

    @DeleteMapping("/api/marketplace/posts/{postId}")
    public ResponseEntity<Void> cancel(@PathVariable Long postId, @RequestParam Long sellerId) {
        currentUser.requireUser(sellerId);
        postService.cancel(postId, sellerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/marketplace/users/{userId}/posts")
    public List<MarketplacePostResponse> findBySeller(@PathVariable Long userId) {
        return postService.findBySeller(userId);
    }
}
