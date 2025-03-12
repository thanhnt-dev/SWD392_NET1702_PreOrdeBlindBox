package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxSeriesItemResponse {
    private Long id;
    private Long seriesId;
    private String itemName;
    private int itemChance;
    private boolean isActive;
    private List<BlindboxAssetResponse> assets;
}
