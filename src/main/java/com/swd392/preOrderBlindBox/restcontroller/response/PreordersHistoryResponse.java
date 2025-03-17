package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreordersHistoryResponse {
    private Long id;
    private String orderCode;
    private PreorderStatus preorderStatus;
    private BigDecimal estimatedTotalAmount;
    private BigDecimal totalAmount;
}
