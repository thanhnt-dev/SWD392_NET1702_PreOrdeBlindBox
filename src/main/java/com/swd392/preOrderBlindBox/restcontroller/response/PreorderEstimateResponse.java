package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderEstimateResponse {
    private BigDecimal estimatedTotalAmount;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private BigDecimal remainingAmount;
    private List<PreorderItemEstimateResponse> items;
}
