package com.swd392.preOrderBlindBox.service;

import com.swd392.preOrderBlindBox.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    List<Category> getCategoriesByParentId(Long parentId);

    Category getCategoryOfBlindboxSeries(Long blindboxSeriesId);

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    Category updateCategory(Category category, Long id);

    void deleteCategory(Long id);
    
}
