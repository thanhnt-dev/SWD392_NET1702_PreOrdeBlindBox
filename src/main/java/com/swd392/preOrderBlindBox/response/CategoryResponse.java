package com.swd392.preOrderBlindBox.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private CategoryResponse parentCategory;
}
