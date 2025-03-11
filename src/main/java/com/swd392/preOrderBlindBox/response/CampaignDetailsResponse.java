package com.swd392.preOrderBlindBox.response;

import com.swd392.preOrderBlindBox.enums.CampaignType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CampaignDetailsResponse {
    private Long id;
    private CampaignType campaignType;
    private LocalDateTime startCampaignTime;
    private LocalDateTime endCampaignTime;
    private int currentPlacedBlindbox;
    private int targetBlindboxQuantity;
    private Integer depositPercent;
    private BigDecimal basePrice;
    private BigDecimal lockedPrice;
    private Long blindboxSeriesId;
    private String blindboxSeriesName;
    private BlindboxSeriesResponse blindboxSeries;
    private List<CampaignTierResponse> campaignTiers;
}