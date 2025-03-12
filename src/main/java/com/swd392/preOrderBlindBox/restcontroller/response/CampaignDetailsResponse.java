package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CampaignDetailsResponse {
    private CampaignType campaignType;
    private LocalDateTime startCampaignTime;
    private LocalDateTime endCampaignTime;
    private int currentPlacedBlindbox;
    private int targetBlindboxQuantity;
    private int depositPercent;
    private BigDecimal basePrice;
    private List<CampaignTierResponse> campaignTiers;
}
