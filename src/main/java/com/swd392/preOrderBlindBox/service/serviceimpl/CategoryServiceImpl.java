package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.entity.Category;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.repository.CategoryRepository;
import com.swd392.preOrderBlindBox.service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        if (!Objects.equals(category.getId(), id)) {
            throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
        existingCategory.setCategoryName(category.getCategoryName());
        existingCategory.setParentCategory(category.getParentCategory());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
