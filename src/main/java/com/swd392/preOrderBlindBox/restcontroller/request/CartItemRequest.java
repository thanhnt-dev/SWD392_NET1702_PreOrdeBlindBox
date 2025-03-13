package com.swd392.preOrderBlindBox.restcontroller.request;

import com.swd392.preOrderBlindBox.common.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CartItemRequest {
  Long blindboxSeriesId;
  ProductType productType;
  int quantity;
}
