package com.swd392.preOrderBlindBox.facade.Impl;

import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.facade.BlindboxSeriesFacade;
import com.swd392.preOrderBlindBox.response.*;
import com.swd392.preOrderBlindBox.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindboxSeriesFacadeImpl implements BlindboxSeriesFacade {
    private final BlindboxSeriesService blindboxSeriesService;
    private final BlindboxSeriesItemService blindboxSeriesItemService;
    private final BlindboxAssetService blindboxAssetService;
    private final BlindboxUnitService blindboxUnitService;
    private final CategoryService categoryService;

    @Override
    public BaseResponse<List<BlindboxSeriesResponse>> getAllBlindboxSeries() {
        List<BlindboxSeries> blindboxSeriesList = blindboxSeriesService.getAllBlindboxSeries();
        List<BlindboxSeriesResponse> responseList = blindboxSeriesList.stream().map(this::toResponse).toList();
        return BaseResponse.build(responseList, true);
    }

    private BlindboxSeriesResponse toResponse(BlindboxSeries blindboxSeries) {
        return BlindboxSeriesResponse.builder()
                .id(blindboxSeries.getId())
                .seriesName(blindboxSeries.getSeriesName())
                .description(blindboxSeries.getDescription())
                .openedAt(blindboxSeries.getOpenedAt())
                .category(toCategoryResponse(blindboxSeries.getCategory()))
                .blindboxUnits(blindboxSeries.getBlindboxUnits().stream().map(this::toBlindboxUnitResponse).toList())
                .blindboxAssets(blindboxSeries.getBlindboxAssets().stream().map(this::toBlindboxAssetResponse).toList())
                .blindboxSeriesItems(blindboxSeries.getBlindboxSeriesItems().stream().map(this::toBlindboxSeriesItemResponse).toList())
                .build();
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .parentCategory(category.getParentCategory() != null ? toCategoryResponse(category.getParentCategory()) : null)
                .build();
    }

    private BlindboxSeriesItemResponse toBlindboxSeriesItemResponse(BlindboxSeriesItem blindboxSeriesItem) {
        return BlindboxSeriesItemResponse.builder()
                .id(blindboxSeriesItem.getId())
                .itemName(blindboxSeriesItem.getItemName())
                .description(blindboxSeriesItem.getDescription())
                .imageUrl(blindboxSeriesItem.getImageUrl())
                .rarityPercentage(blindboxSeriesItem.getRarityPercentage())
                .build();
    }

    private BlindboxUnitResponse toBlindboxUnitResponse(BlindboxUnit blindboxUnit) {
        return BlindboxUnitResponse.builder()
                .id(blindboxUnit.getId())
                .title(blindboxUnit.getTitle())
                .price(blindboxUnit.getPrice())
                .discountPercent(blindboxUnit.getDiscountPercent())
                .stockQuantity(blindboxUnit.getStockQuantity())
                .quantityPerPackage(blindboxUnit.getQuantityPerPackage())
                .build();
    }

    private BlindboxAssetResponse toBlindboxAssetResponse(BlindboxAsset blindboxAsset) {
        return BlindboxAssetResponse.builder()
                .id(blindboxAsset.getId())
                .mediaKey(blindboxAsset.getMediaKey())
                .build();
    }
}
