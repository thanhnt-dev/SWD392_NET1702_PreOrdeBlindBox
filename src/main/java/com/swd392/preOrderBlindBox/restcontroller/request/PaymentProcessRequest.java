package com.swd392.preOrderBlindBox.restcontroller.request;

import com.swd392.preOrderBlindBox.common.enums.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessRequest {
    @NotNull(message = "Preorder ID is required")
    @Schema(description = "ID of the preorder to process payment for", example = "1")
    private Long preorderId;

    @Schema(description = "Platform initiating the payment (WEB or MOBILE)", example = "WEB", defaultValue = "WEB")
    private Platform platform = Platform.WEB;
}