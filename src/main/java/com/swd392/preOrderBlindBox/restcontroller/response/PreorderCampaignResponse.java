package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderCampaignResponse {
    Long id;
    BlindboxSeriesResponse series;
    CampaignType campaignType;
    LocalDateTime startCampaignTime;
    LocalDateTime endCampaignTime;
    boolean isActive;
    int totalDiscountedUnits;
}
