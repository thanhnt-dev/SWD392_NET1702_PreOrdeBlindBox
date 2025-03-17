package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.Cart;
import com.swd392.preOrderBlindBox.entity.CartItem;
import com.swd392.preOrderBlindBox.restcontroller.request.CartItemRequest;
import java.math.BigDecimal;
import java.util.List;

public interface CartService {
  Cart getOrCreateCart();

  List<CartItem> getCartItems();

  Cart addToCart(CartItemRequest cartItemRequest);

  Cart updateCartItemQuantity(Long cartItemId, int quantity);

  Cart removeCartItem(Long cartItemId);

  Cart clearCart();

  BigDecimal calculateCartTotal();
}
