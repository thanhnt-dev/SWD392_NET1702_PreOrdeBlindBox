package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartResponse {
    Long id;
    BigDecimal totalPrice;
    List<CartItemResponse> cartItems;
}
