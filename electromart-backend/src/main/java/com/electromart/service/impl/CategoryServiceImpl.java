package com.electromart.service.impl;

import com.electromart.dto.request.CategoryRequest;
import com.electromart.dto.response.CategoryResponse;
import com.electromart.entity.Category;
import com.electromart.exception.DuplicateResourceException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.mapper.CategoryMapper;
import com.electromart.repository.CategoryRepository;
import com.electromart.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("A category named '" + request.getName() + "' already exists");
        }
        String slug = toSlug(request.getName());
        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .active(true)
                .build();
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
        category.setName(request.getName());
        category.setSlug(toSlug(request.getName()));
        category.setDescription(request.getDescription());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
        // Soft delete: a category with existing products can't be hard-deleted
        // without either cascading (destructive) or orphaning products (breaks
        // the NOT NULL FK). Deactivating hides it from browsing/creation without
        // touching any product that references it.
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(CategoryMapper::toResponse).toList();
    }

    private String toSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w\\s-]").matcher(normalized).replaceAll("");
        return slug.trim().toLowerCase().replaceAll("[\\s_]+", "-");
    }
}