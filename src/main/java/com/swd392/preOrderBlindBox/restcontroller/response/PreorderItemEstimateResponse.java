package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.ProductType;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderItemEstimateResponse {
    private BlindboxSeriesResponse series;
    private ProductType productType;
    private int quantity;
    private BigDecimal discountedTotalPrice;
}
