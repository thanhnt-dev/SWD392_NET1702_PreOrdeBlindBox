package com.swd392.preOrderBlindBox.response;

import com.swd392.preOrderBlindBox.enums.CampaignType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CampaignResponse {
    private CampaignType campaignType;
    private LocalDateTime startCampaignTime;
    private LocalDateTime endCampaignTime;
    private int currentPlacedBlindbox;
    private int targetBlindboxQuantity;
    private int depositPercent;
    private BigDecimal basePrice;
}
