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
        List<BlindboxSeriesResponse> responseList = blindboxSeriesList.stream().map(this::toBlindboxSeriesResponse).toList();
        return BaseResponse.build(responseList, true);
    }

    @Override
    public BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesWithDetailsById(Long id) {
        BlindboxSeries blindboxSeries = blindboxSeriesService.getBlindboxSeriesById(id);
        BlindboxSeriesDetailsResponse response = toBlindboxSeriesDetailsResponse(blindboxSeries);
        return BaseResponse.build(response, true);
    }


    private BlindboxSeriesDetailsResponse toBlindboxSeriesDetailsResponse(BlindboxSeries blindboxSeries) {
        return BlindboxSeriesDetailsResponse.builder()
                .id(blindboxSeries.getId())
                .seriesName(blindboxSeries.getSeriesName())
                .description(blindboxSeries.getDescription())
                .openedAt(blindboxSeries.getOpenedAt())
                .category(toCategoryResponse(blindboxSeries.getCategory()))
                .blindboxSeriesItems(toBlindboxSeriesItemListResponse(blindboxSeries.getId()))
                .blindboxUnits(toBlindboxUnitListResponse(blindboxSeries.getId()))
                .blindboxAssets(toBlindboxAssetListResponse(blindboxSeries.getId()))
                .build();
    }

    private BlindboxSeriesResponse toBlindboxSeriesResponse(BlindboxSeries blindboxSeries) {
        return BlindboxSeriesResponse.builder()
                .id(blindboxSeries.getId())
                .seriesName(blindboxSeries.getSeriesName())
                .description(blindboxSeries.getDescription())
                .openedAt(blindboxSeries.getOpenedAt())
                .category(toCategoryResponse(blindboxSeries.getCategory()))
                .build();
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .parentCategory(category.getParentCategory() != null ? toCategoryResponse(category.getParentCategory()) : null)
                .build();
    }

    private List<BlindboxSeriesItemResponse> toBlindboxSeriesItemListResponse(Long blindboxSeriesItemId) {
        List<BlindboxSeriesItem> blindboxSeriesItemList = blindboxSeriesItemService.getBlindboxSeriesItemsByBlindboxSeriesId(blindboxSeriesItemId);

        return blindboxSeriesItemList
                .stream()
                .map(this::toBlindboxSeriesItemResponse)
                .toList();
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

    private List<BlindboxUnitResponse> toBlindboxUnitListResponse(Long blindboxSeriesItemId) {
        List<BlindboxUnit> blindboxUnitList = blindboxUnitService.getBlindboxUnitsByBlindboxSeriesId(blindboxSeriesItemId);

        return blindboxUnitList
                .stream()
                .map(this::toBlindboxUnitResponse)
                .toList();
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

    private List<BlindboxAssetResponse> toBlindboxAssetListResponse(Long blindboxSeriesId) {
        List<BlindboxAsset> blindboxAssetList = blindboxAssetService.getBlindboxAssetsByBlindboxSeriesId(blindboxSeriesId);

        return blindboxAssetList
                .stream()
                .map(this::toBlindboxAssetResponse)
                .toList();
    }

    private BlindboxAssetResponse toBlindboxAssetResponse(BlindboxAsset blindboxAsset) {
        return BlindboxAssetResponse.builder()
                .id(blindboxAsset.getId())
                .mediaKey(blindboxAsset.getMediaKey())
                .build();
    }
}
