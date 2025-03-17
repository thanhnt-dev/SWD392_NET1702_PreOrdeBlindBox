package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentSummaryResponse {
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime issuedAt;
    private boolean isDeposit;
}
