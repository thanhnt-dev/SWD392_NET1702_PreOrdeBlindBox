package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransactionResponse {
    private Long id;
    private Long userId;
    private Long preorderId;
    private String transactionCode;
    private TransactionType transactionType;
    private String content;
    private BigDecimal transactionAmount;
    private TransactionStatus transactionStatus;
    private boolean isDeposit;
    private TransactionResponse relatedTransaction;
    private LocalDateTime createdAt;
}
