package com.swd392.preOrderBlindBox.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxSeriesDetailsResponse {
    private Long id;
    private String seriesName;
    private LocalDateTime openedAt;
    private String description;
    private List<BlindboxSeriesItemResponse> blindboxSeriesItems;
    private List<BlindboxAssetResponse> blindboxAssets;
    private List<BlindboxUnitResponse> blindboxUnits;
    private CategoryResponse category;
}
