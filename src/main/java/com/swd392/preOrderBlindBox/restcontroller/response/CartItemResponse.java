package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.ProductType;
import java.math.BigDecimal;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItemResponse {
  private Long id;
  private BlindboxSeriesResponse series;
  private ProductType productType;
  private int quantity;
  private BigDecimal price;
  private BigDecimal discountedPrice;
  private int discountPercent;
  private CampaignType itemCampaignType;
}
