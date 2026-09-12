package com.campuscrate.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campuscrate.dto.ItemRequest;
import com.campuscrate.dto.ItemResponse;
import com.campuscrate.service.ItemService;
import com.campuscrate.security.CurrentUser;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final CurrentUser currentUser;

    public ItemController(ItemService itemService, CurrentUser currentUser) {
        this.itemService = itemService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<ItemResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String itemType) {
        return itemService.findAll(search, status, categoryId, locationId, itemType);
    }

    @GetMapping("/{id}")
    public ItemResponse findById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest request) {
        currentUser.requireUser(request.reportedBy());
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(request));
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        currentUser.requireUser(itemService.findById(id).reportedBy());
        currentUser.requireUser(request.reportedBy());
        return itemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        currentUser.requireUser(itemService.findById(id).reportedBy());
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
