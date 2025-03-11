package com.swd392.preOrderBlindBox.request;

import com.swd392.preOrderBlindBox.enums.CampaignType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CampaignCreateRequest {
    @NotNull(message = "Blindbox series ID is required")
    @Schema(description = "Blindbox series ID", example = "1")
    private Long blindboxSeriesId;

    @NotNull(message = "Campaign type is required")
    @Schema(description = "Campaign type (MILESTONE or GROUP)", example = "MILESTONE")
    private CampaignType campaignType;

    @NotNull(message = "Start campaign time is required")
    @Schema(description = "Start campaign time", example = "2025-03-01T08:00:00")
    private LocalDateTime startCampaignTime;

    @NotNull(message = "End campaign time is required")
    @Schema(description = "End campaign time", example = "2025-03-31T23:59:59")
    private LocalDateTime endCampaignTime;

    @NotNull(message = "Target blindbox quantity is required")
    @Min(value = 1, message = "Target blindbox quantity must be at least 1")
    @Schema(description = "Target blindbox quantity", example = "100")
    private Integer targetBlindboxQuantity;

    @NotNull(message = "Base price is required")
    @Min(value = 0, message = "Base price must be at least 0")
    @Schema(description = "Base price", example = "10.00")
    private BigDecimal basePrice;

    @Schema(description = "Deposit percent for GROUP type campaigns", example = "50")
    private Integer depositPercent;

    @Schema(description = "Campaign tiers")
    private List<CampaignTierCreateRequest> tiers;
}