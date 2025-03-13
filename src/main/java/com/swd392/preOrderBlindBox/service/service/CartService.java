package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.Cart;
import com.swd392.preOrderBlindBox.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;

public interface CartService {
  Cart getOrCreateCart();

  List<CartItem> getCartItems(Long cartId);

  CartItem addToCart(CartItem cartItem);

  CartItem updateCartItemQuantity(Long cartItemId, int quantity);

  void removeCartItem(Long cartItemId);

  void clearCart(Long cartId);

  BigDecimal calculateCartTotal(Long cartId);
}
