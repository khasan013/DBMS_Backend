package com.campuscrate.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscrate.dto.CategoryRequest;
import com.campuscrate.dto.CategoryResponse;
import com.campuscrate.exception.CategoryNotFoundException;
import com.campuscrate.exception.DuplicateCategoryException;
import com.campuscrate.model.Category;
import com.campuscrate.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long categoryId) {
        return toResponse(findCategory(categoryId));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateCategoryException(request.name());
        }

        try {
            return toResponse(categoryRepository.create(new Category(null, request.name())));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCategoryException(request.name());
        }
    }

    @Transactional
    public CategoryResponse update(Long categoryId, CategoryRequest request) {
        findCategory(categoryId);
        if (categoryRepository.existsByNameExceptId(request.name(), categoryId)) {
            throw new DuplicateCategoryException(request.name());
        }

        try {
            categoryRepository.update(categoryId, request.name());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCategoryException(request.name());
        }
        return new CategoryResponse(categoryId, request.name());
    }

    @Transactional
    public void delete(Long categoryId) {
        findCategory(categoryId);
        categoryRepository.delete(categoryId);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getCategoryId(), category.getName());
    }
}