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
public class BlindboxSeriesManagementDetailsResponse {
    private Long id;
    private String seriesName;
    private String description;
    private BigDecimal packagePrice;
    private BigDecimal boxPrice;
    private boolean isActive;
    private List<BlindboxAssetResponse> assets;
    private List<BlindboxPackageResponse> packages;
    private List<BlindboxSeriesItemResponse> items;
    private List<PreorderCampaignResponse> campaigns;
}