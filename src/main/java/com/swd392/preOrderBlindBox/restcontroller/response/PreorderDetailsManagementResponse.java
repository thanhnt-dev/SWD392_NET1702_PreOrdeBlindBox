package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderDetailsManagementResponse {
    private Long id;
    private String username;
    private String orderCode;
    private String deliveryCode;
    private String userAddress;
    private String phoneNumber;
    private PreorderStatus preorderStatus;
    private BigDecimal estimatedTotalAmount;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private BigDecimal remainingAmount;
    private LocalDateTime createdAt;
    private List<PreorderItemResponse> items;
    private List<TransactionResponse> transactions;
}
