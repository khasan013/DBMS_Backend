package com.campuscrate.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.ItemRequest;
import com.campuscrate.dto.ItemResponse;
import com.campuscrate.exception.ItemReferenceNotFoundException;
import com.campuscrate.exception.ItemNotFoundException;
import com.campuscrate.model.Item;
import com.campuscrate.repository.CategoryRepository;
import com.campuscrate.repository.ItemRepository;
import com.campuscrate.repository.LocationRepository;
import com.campuscrate.repository.UserRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
            CategoryRepository categoryRepository, LocationRepository locationRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
    }

    public List<ItemResponse> findAll(String search, String status, Long categoryId,
            Long locationId, String itemType) {
        return itemRepository.findAll(search, status, categoryId, locationId, itemType).stream()
                .map(this::toResponse)
                .toList();
    }

    public ItemResponse findById(Long itemId) {
        return toResponse(findItem(itemId));
    }

    @Transactional
    public ItemResponse create(ItemRequest request) {
        validateReferences(request);
        Item item = new Item(null, request.title(), request.description(), request.itemType(),
                request.imageUrl(), request.status(), null, request.reportedBy(),
                request.categoryId(), request.locationId());
        return toResponse(itemRepository.create(item));
    }

    @Transactional
    public ItemResponse update(Long itemId, ItemRequest request) {
        findItem(itemId);
        validateReferences(request);
        Item item = new Item(itemId, request.title(), request.description(), request.itemType(),
                request.imageUrl(), request.status(), null, request.reportedBy(),
                request.categoryId(), request.locationId());
        itemRepository.update(itemId, item);
        item.setCreatedAt(findItem(itemId).getCreatedAt());
        return toResponse(item);
    }

    @Transactional
    public void delete(Long itemId) {
        findItem(itemId);
        itemRepository.delete(itemId);
    }

    @Transactional
    public ItemResponse updateStatusByAdmin(Long itemId, String status) {
        String normalized = status.trim().toUpperCase();
        if (!Set.of("LOST", "FOUND", "RETURNED", "RESOLVED").contains(normalized)) {
            throw new com.campuscrate.exception.InvalidRequestException("Item status must be LOST, FOUND, RETURNED, or RESOLVED");
        }
        findItem(itemId);
        itemRepository.updateStatus(itemId, normalized);
        return findById(itemId);
    }

    private void validateReferences(ItemRequest request) {
        if (userRepository.findById(request.reportedBy()).isEmpty()) {
            throw new ItemReferenceNotFoundException("user", request.reportedBy());
        }
        if (categoryRepository.findById(request.categoryId()).isEmpty()) {
            throw new ItemReferenceNotFoundException("category", request.categoryId());
        }
        if (locationRepository.findById(request.locationId()).isEmpty()) {
            throw new ItemReferenceNotFoundException("location", request.locationId());
        }
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(item.getItemId(), item.getTitle(), item.getDescription(),
                item.getItemType(), item.getImageUrl(), item.getStatus(), item.getCreatedAt(),
                item.getReportedBy(), item.getCategoryId(), item.getLocationId());
    }
}
