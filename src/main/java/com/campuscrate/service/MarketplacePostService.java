package com.campuscrate.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.MarketplacePostCreateRequest;
import com.campuscrate.dto.MarketplacePostResponse;
import com.campuscrate.dto.MarketplacePostUpdateRequest;
import com.campuscrate.exception.MarketplaceConflictException;
import com.campuscrate.exception.MarketplaceForbiddenException;
import com.campuscrate.exception.MarketplaceInvalidRequestException;
import com.campuscrate.exception.MarketplacePostNotFoundException;
import com.campuscrate.exception.MarketplaceReferenceNotFoundException;
import com.campuscrate.model.MarketplacePost;
import com.campuscrate.repository.CategoryRepository;
import com.campuscrate.repository.LocationRepository;
import com.campuscrate.repository.MarketplacePostRepository;
import com.campuscrate.repository.UserRepository;

@Service
public class MarketplacePostService {

    private static final String ACTIVE = "ACTIVE";
    private static final String FIXED_PRICE = "FIXED_PRICE";
    private static final String AUCTION = "AUCTION";

    private final MarketplacePostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public MarketplacePostService(MarketplacePostRepository postRepository,
            UserRepository userRepository, CategoryRepository categoryRepository,
            LocationRepository locationRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public MarketplacePostResponse create(MarketplacePostCreateRequest request) {
        validateReferences(request.sellerId(), request.categoryId(), request.locationId());
        validateOffer(request.sellingType(), request.fixedPrice(), request.startingPrice(),
                request.auctionStart(), request.auctionEnd());

        MarketplacePost post = new MarketplacePost(null, request.sellerId(), request.categoryId(),
                request.locationId(), request.title(), request.description(), request.condition(),
                request.sellingType(), request.fixedPrice(), request.startingPrice(),
                request.auctionStart(), request.auctionEnd(), ACTIVE, null, null);
        try {
            MarketplacePost created = postRepository.create(post);
            return toResponse(findPost(created.getPostId()));
        } catch (DataIntegrityViolationException exception) {
            throw new MarketplaceConflictException("Marketplace post violates a database constraint");
        }
    }

    public List<MarketplacePostResponse> findAll(String search, Long categoryId, Long locationId,
            String sellingType, BigDecimal minPrice, BigDecimal maxPrice) {
        validatePriceRange(minPrice, maxPrice);
        validateSellingTypeIfPresent(sellingType);
        return postRepository.findAll(search, categoryId, locationId, sellingType, minPrice, maxPrice,
                false, null).stream().map(this::toResponse).toList();
    }

    public List<MarketplacePostResponse> findActive() {
        return postRepository.findAll(null, null, null, null, null, null, true, null)
                .stream().map(this::toResponse).toList();
    }

    public List<MarketplacePostResponse> findBySeller(Long sellerId) {
        if (userRepository.findById(sellerId).isEmpty()) {
            throw new MarketplaceReferenceNotFoundException("seller", sellerId);
        }
        return postRepository.findAll(null, null, null, null, null, null, false, sellerId)
                .stream().map(this::toResponse).toList();
    }

    public MarketplacePostResponse findById(Long postId) {
        return toResponse(findPost(postId));
    }

    @Transactional
    public MarketplacePostResponse update(Long postId, Long actingSellerId,
            MarketplacePostUpdateRequest request) {
        MarketplacePost existing = findPost(postId);
        verifyOwner(existing, actingSellerId);
        if (!ACTIVE.equals(existing.getStatus())) {
            throw new MarketplaceConflictException("Only ACTIVE posts can be edited");
        }
        if (!existing.getSellingType().equals(request.sellingType())) {
            throw new MarketplaceInvalidRequestException("Selling type cannot be changed after creation");
        }
        validateReferences(existing.getSellerId(), request.categoryId(), request.locationId());
        validateOffer(request.sellingType(), request.fixedPrice(), request.startingPrice(),
                request.auctionStart(), request.auctionEnd());

        MarketplacePost updated = new MarketplacePost(postId, existing.getSellerId(), request.categoryId(),
                request.locationId(), request.title(), request.description(), request.condition(),
                request.sellingType(), request.fixedPrice(), request.startingPrice(),
                request.auctionStart(), request.auctionEnd(), existing.getStatus(),
                existing.getCreatedAt(), existing.getUpdatedAt());
        try {
            postRepository.update(postId, updated);
            return toResponse(findPost(postId));
        } catch (DataIntegrityViolationException exception) {
            throw new MarketplaceConflictException("Marketplace post violates a database constraint");
        }
    }

    @Transactional
    public void cancel(Long postId, Long actingSellerId) {
        MarketplacePost existing = findPost(postId);
        verifyOwner(existing, actingSellerId);
        if (!ACTIVE.equals(existing.getStatus())) {
            throw new MarketplaceConflictException("Only ACTIVE posts can be cancelled");
        }
        postRepository.cancel(postId);
    }

    private void validateReferences(Long sellerId, Long categoryId, Long locationId) {
        if (userRepository.findById(sellerId).isEmpty()) {
            throw new MarketplaceReferenceNotFoundException("seller", sellerId);
        }
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new MarketplaceReferenceNotFoundException("category", categoryId);
        }
        if (locationRepository.findById(locationId).isEmpty()) {
            throw new MarketplaceReferenceNotFoundException("location", locationId);
        }
    }

    private void validateOffer(String sellingType, BigDecimal fixedPrice, BigDecimal startingPrice,
            LocalDateTime auctionStart, LocalDateTime auctionEnd) {
        if (sellingType == null) {
            throw new MarketplaceInvalidRequestException("Selling type is required");
        }
        if (FIXED_PRICE.equals(sellingType)) {
            if (fixedPrice == null || fixedPrice.signum() <= 0 || startingPrice != null
                    || auctionStart != null || auctionEnd != null) {
                throw new MarketplaceInvalidRequestException("Invalid FIXED_PRICE fields");
            }
        } else if (AUCTION.equals(sellingType)) {
            if (startingPrice == null || startingPrice.signum() <= 0 || fixedPrice != null
                    || auctionStart == null || auctionEnd == null
                    || !auctionEnd.isAfter(auctionStart)) {
                throw new MarketplaceInvalidRequestException("Invalid AUCTION fields");
            }
        } else {
            throw new MarketplaceInvalidRequestException("Selling type must be FIXED_PRICE or AUCTION");
        }
    }

    private void validateSellingTypeIfPresent(String sellingType) {
        if (sellingType != null && !sellingType.equals(FIXED_PRICE) && !sellingType.equals(AUCTION)) {
            throw new MarketplaceInvalidRequestException("Selling type must be FIXED_PRICE or AUCTION");
        }
    }

    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && minPrice.signum() < 0 || maxPrice != null && maxPrice.signum() < 0) {
            throw new MarketplaceInvalidRequestException("Prices cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new MarketplaceInvalidRequestException("minPrice cannot exceed maxPrice");
        }
    }

    private void verifyOwner(MarketplacePost post, Long actingSellerId) {
        if (!post.getSellerId().equals(actingSellerId)) {
            throw new MarketplaceForbiddenException("You can modify only your own marketplace posts");
        }
    }

    private MarketplacePost findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new MarketplacePostNotFoundException(postId));
    }

    private MarketplacePostResponse toResponse(MarketplacePost post) {
        return new MarketplacePostResponse(post.getPostId(), post.getSellerId(), post.getCategoryId(),
                post.getLocationId(), post.getTitle(), post.getDescription(), post.getCondition(),
                post.getSellingType(), post.getFixedPrice(), post.getStartingPrice(),
                post.getAuctionStart(), post.getAuctionEnd(), post.getStatus(), post.getCreatedAt(),
                post.getUpdatedAt());
    }
}