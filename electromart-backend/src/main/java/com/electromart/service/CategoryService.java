package com.electromart.service;

import com.electromart.dto.request.CategoryRequest;
import com.electromart.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
    List<CategoryResponse> getAll();
}