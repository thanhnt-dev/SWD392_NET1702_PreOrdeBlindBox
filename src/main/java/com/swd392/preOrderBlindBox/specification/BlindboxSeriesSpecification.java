package com.swd392.preOrderBlindBox.specification;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import org.springframework.data.jpa.domain.Specification;

public class BlindboxSeriesSpecification {

    public static Specification<BlindboxSeries> hasSeriesName(String seriesName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("seriesName"), "%" + seriesName + "%");
    }

    public static Specification<BlindboxSeries> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    // Add more specifications as needed
}