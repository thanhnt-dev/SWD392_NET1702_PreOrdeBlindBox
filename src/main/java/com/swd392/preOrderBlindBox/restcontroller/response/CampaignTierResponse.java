package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.TierStatus;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CampaignTierResponse {
    Long id;
    String alias;
    int currentUnitsCount;
    int thresholdQuantity;
    int tierOrder;
    int discountPercent;
    TierStatus tierStatus;
    boolean isActive;
}
