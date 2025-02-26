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
public class BlindboxUnitResponse {
    private Long id;
    private String title;
    private int discountPercent;
    private BigDecimal price;
    private int quantityPerPackage;
    private int stockQuantity;
}
