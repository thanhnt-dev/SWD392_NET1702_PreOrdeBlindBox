package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.BaseEntity;
import com.swd392.preOrderBlindBox.entity.Cart;
import com.swd392.preOrderBlindBox.entity.CartItem;
import com.swd392.preOrderBlindBox.entity.User;
import com.swd392.preOrderBlindBox.repository.repository.CartItemRepository;
import com.swd392.preOrderBlindBox.repository.repository.CartRepository;
import com.swd392.preOrderBlindBox.service.service.CartService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final UserServiceImpl userService;

  @Override
  @Transactional
  public Cart getOrCreateCart() {
    Optional<User> currentUser = userService.getCurrentUser();
    return cartRepository
        .findByUserId(currentUser.map(BaseEntity::getId).orElse(null))
        .orElseGet(
            () -> {
              Cart newCart = new Cart();
              newCart.setUser(currentUser.orElse(null));
              return cartRepository.save(newCart);
            });
  }

  @Override
  public List<CartItem> getCartItems(Long cartId) {
    return cartItemRepository.findByCartId(cartId);
  }

  @Override
  @Transactional
  public CartItem addToCart(CartItem cartItem) {
    Cart cart = getOrCreateCart();
    cartItem.setCart(cart);

    CartItem existingItem =
        cartItemRepository
            .findByCartIdAndSeriesId(cart.getId(), cartItem.getSeries().getId())
            .orElse(null);

    if (existingItem != null) {
      existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
      return cartItemRepository.save(existingItem);
    }

    return cartItemRepository.save(cartItem);
  }

  @Override
  @Transactional
  public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    CartItem cartItem =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    verifyCartOwnership(cartItem);

    cartItem.setQuantity(quantity);
    return cartItemRepository.save(cartItem);
  }

  @Override
  @Transactional
  public void removeCartItem(Long cartItemId) {
    CartItem cartItem =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    verifyCartOwnership(cartItem);

    cartItemRepository.deleteById(cartItemId);
  }

  @Override
  @Transactional
  public void clearCart(Long cartId) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    Optional<User> currentUser = userService.getCurrentUser();
    if (currentUser.isPresent()
        && (cart.getUser() == null || !cart.getUser().getId().equals(currentUser.get().getId()))) {
      throw new SecurityException("Cannot clear cart that doesn't belong to current user");
    }

    cartItemRepository.deleteByCartId(cartId);
  }

  @Override
  public BigDecimal calculateCartTotal(Long cartId) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    verifyCartOwnership(cart);

    List<CartItem> cartItems = getCartItems(cartId);

    return cartItems.stream()
        .map(
            item -> {
              BigDecimal price = item.getPrice();
              int quantity = item.getQuantity();
              return price.multiply(BigDecimal.valueOf(quantity));
            })
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }

  private void verifyCartOwnership(CartItem cartItem) {
    if (cartItem == null || cartItem.getCart() == null) {
      throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
    }

    verifyCartOwnership(cartItem.getCart());
  }

  private void verifyCartOwnership(Cart cart) {
    Optional<User> currentUser = userService.getCurrentUser();
    if (currentUser.isPresent()
        && (cart.getUser() == null || !cart.getUser().getId().equals(currentUser.get().getId()))) {
      throw new SecurityException(String.valueOf(ErrorCode.UNAUTHORIZED_CART_ACCESS));
    }
  }
}
