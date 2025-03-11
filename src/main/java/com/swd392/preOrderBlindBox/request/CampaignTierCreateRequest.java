package com.swd392.preOrderBlindBox.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CampaignTierCreateRequest {
    @NotBlank(message = "Tier name is required")
    @Schema(description = "Tier name", example = "Bronze Tier")
    private String tierName;

    @NotNull(message = "Min quantity is required")
    @Min(value = 0, message = "Min quantity must be at least 0")
    @Schema(description = "Min quantity", example = "0")
    private Integer minQuantity;

    @Schema(description = "Max quantity (must be null for GROUP type)", example = "50")
    private Integer maxQuantity;

    @NotNull(message = "Discount percent is required")
    @Min(value = 0, message = "Discount percent must be at least 0")
    @Max(value = 100, message = "Discount percent must not exceed 100")
    @Schema(description = "Discount percent", example = "10")
    private Integer discountPercent;
}