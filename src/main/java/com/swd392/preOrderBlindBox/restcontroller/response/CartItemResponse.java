package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.ProductType;
import com.swd392.preOrderBlindBox.entity.Cart;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItemResponse {
    private Long id;
    private Cart cart;
    private BlindboxSeriesResponse series;
    private ProductType productType;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private int discountPercent;
    private CampaignType itemCampaignType;
}
