package com.swd392.preOrderBlindBox.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxSeriesItemResponse {
    private Long id;
    private String itemName;
    private String imageUrl;
    private String description;
    private BigDecimal rarityPercentage;
}
