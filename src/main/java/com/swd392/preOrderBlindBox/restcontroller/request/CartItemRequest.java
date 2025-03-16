package com.swd392.preOrderBlindBox.restcontroller.request;

import com.swd392.preOrderBlindBox.common.enums.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CartItemRequest {
  @NotNull(message = "Series ID cannot be null")
  Long blindboxSeriesId;

  @NotNull(message = "Product type cannot be null")
  ProductType productType;

  @NotNull(message = "Quantity cannot be null")
  @DecimalMin(value = "1", message = "Cart item quantity must be at least 1")
  int quantity;
}
