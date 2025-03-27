package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.enums.ProductType;
import com.swd392.preOrderBlindBox.common.enums.TierStatus;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.common.util.Util;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.repository.repository.CartItemRepository;
import com.swd392.preOrderBlindBox.repository.repository.CartRepository;
import com.swd392.preOrderBlindBox.restcontroller.request.CartItemRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.CartItemResponse;
import com.swd392.preOrderBlindBox.service.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final UserService userService;
  private final BlindboxSeriesService blindboxSeriesService;
  private final PreorderCampaignService preorderCampaignService;
  private final CampaignTierService campaignTierService;
  private final ModelMapper modelMapper;

  @Override
  public Cart getOrCreateCart() {
    User currentUser =
        userService
            .getCurrentUser()
            .orElseThrow(() -> new SecurityException("User must be logged in to access the cart"));

    return cartRepository
        .findByUserId(currentUser.getId())
        .orElseGet(
            () -> {
              Cart newCart = new Cart();
              newCart.setUser(currentUser);
              newCart.setTotalPrice(BigDecimal.ZERO);
              return cartRepository.save(newCart);
            });
  }

  @Override
  public List<CartItem> getCartItems() {
    Cart cart = getOrCreateCart();
    return cartItemRepository.findByCartId(cart.getId());
  }

  @Override
  @Transactional
  public Cart addToCart(CartItemRequest cartItemRequest) {
    Cart cart = getOrCreateCart();
    CartItem cartItem = createCartItemFromRequest(cartItemRequest, cart);

    applyCampaignDiscount(cartItem);
    validateProductAvailability(cartItem);

    updateOrSaveCartItem(cartItem, cart);

    return updateCartTotal(cart);
  }

  @Override
  @Transactional
  public Cart updateCartItemQuantity(Long cartItemId, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    Cart cart = getOrCreateCart();

    CartItem cartItem =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    verifyCartItemOwnership(cartItem);
    validateProductAvailability(cartItem, quantity);
    if (cartItem.getItemCampaignType() != null) {
      validateDiscountedUnitsAvailability(cartItem, quantity);
    }

    cartItem.setQuantity(quantity);
    cartItemRepository.save(cartItem);

    return updateCartTotal(cart);
  }

  @Override
  @Transactional
  public Cart removeCartItem(Long cartItemId) {
    Cart cart = getOrCreateCart();
    CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    verifyCartItemOwnership(cartItem);

    cart.getCartItems().remove(cartItem);

    return updateCartTotal(cart);
  }

  @Override
  @Transactional
  public Cart clearCart() {
    Cart cart = getOrCreateCart();

    if (cart.getCartItems() != null) {
      cart.getCartItems().clear();
    } else {
      cart.setCartItems(new ArrayList<>());
    }

    return updateCartTotal(cart);
  }

  @Override
  public BigDecimal calculateCartTotal() {
    Cart cart = getOrCreateCart();
    verifyCartOwnership(cart);
    List<CartItem> cartItems = getCartItems();

    return cartItems.stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal calculateItemTotal(CartItem item) {
    BigDecimal discountedPrice = calculateItemDiscountedPrice(item);
    return Util.calculatePriceWithCoefficient(discountedPrice, BigDecimal.valueOf(item.getQuantity()));
  }

  @Override
  public BigDecimal calculateItemDiscountedPrice(CartItem item) {
    return calculateDiscountedPrice(item.getPrice(), item.getDiscountPercent());
  }

  @Override
  public BigDecimal calculateItemDiscountedPrice(CartItemResponse item) {
    return calculateDiscountedPrice(item.getPrice(), item.getDiscountPercent());
  }

  private BigDecimal calculateDiscountedPrice(BigDecimal price, double discountPercent) {
    BigDecimal discountFactor = BigDecimal.valueOf(discountPercent)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    return Util.calculatePriceWithCoefficient(price, BigDecimal.ONE.subtract(discountFactor));
  }

  private CartItem createCartItemFromRequest(CartItemRequest cartItemRequest, Cart cart) {
    CartItem cartItem = modelMapper.map(cartItemRequest, CartItem.class);
    BlindboxSeries series = blindboxSeriesService.getBlindboxSeriesById(cartItemRequest.getBlindboxSeriesId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    cartItem.setSeries(series);
    if (cartItem.getProductType() == null) {
      throw new IllegalArgumentException("Product type is required");
    }
    BigDecimal price = cartItem.getProductType() == ProductType.PACKAGE ? series.getPackagePrice() : series.getBoxPrice();
    if (price == null) {
      throw new IllegalStateException("Price for series ID " + series.getId() + " is null");
    }
    cartItem.setPrice(price);
    cartItem.setCart(cart);
    cartItem.setItemCampaignType(preorderCampaignService.getOngoingCampaignOfBlindboxSeries(cartItemRequest
                    .getBlindboxSeriesId())
            .map(PreorderCampaign::getCampaignType)
            .orElse(null));
    return cartItem;
  }

  private void applyCampaignDiscount(CartItem cartItem) {
    PreorderCampaign ongoingCampaign = preorderCampaignService
            .getOngoingCampaignOfBlindboxSeries(cartItem.getSeries().getId())
            .orElse(null);
    if (ongoingCampaign != null && cartItem.getItemCampaignType() != null) {
      validateDiscountedUnitsAvailability(cartItem);
      cartItem.setDiscountPercent(
              preorderCampaignService.getDiscountOfActiveTierOfOnGoingCampaign(ongoingCampaign.getId()));
    }
  }

  private void updateOrSaveCartItem(CartItem cartItem, Cart cart) {
    CartItem existingItem = cartItemRepository
            .findByCartIdAndSeriesIdAndProductType(
                    cart.getId(),
                    cartItem.getSeries().getId(),
                    cartItem.getProductType()
            )
            .orElse(null);

    if (existingItem != null) {
      int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
      validateProductAvailability(existingItem, newQuantity);
      if (newQuantity <= 0) {
        cartItemRepository.delete(existingItem);
        cart.getCartItems().remove(existingItem);
      } else {
        existingItem.setQuantity(newQuantity);
        existingItem.setPrice(cartItem.getPrice());
        cartItemRepository.save(existingItem);
      }
    } else if (cartItem.getQuantity() > 0) {
      cartItem.setCart(cart);
      cart.getCartItems().add(cartItem);
      cartItemRepository.save(cartItem);
    }
  }

  private Cart updateCartTotal(Cart cart) {
    cart.setTotalPrice(calculateCartTotal());
    return cartRepository.save(cart);
  }

  private void verifyCartItemOwnership(CartItem cartItem) {
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

  private void validateProductAvailability(CartItem cartItem) {
    switch (cartItem.getProductType()) {
      case PACKAGE:
        if (blindboxSeriesService.getAvailablePackageQuantityOfSeries(cartItem.getSeries().getId())
            < cartItem.getQuantity()) {
          throw new IllegalArgumentException("Not enough package units available");
        }
        break;
      case BOX:
        if (blindboxSeriesService.getAvailableBlindboxQuantityOfSeries(cartItem.getSeries().getId())
            < cartItem.getQuantity()) {
          throw new IllegalArgumentException("Not enough box units available");
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid product type");
    }
  }

  private void validateProductAvailability(CartItem cartItem, int quantity) {
    switch (cartItem.getProductType()) {
      case PACKAGE:
        if (blindboxSeriesService.getAvailablePackageQuantityOfSeries(cartItem.getSeries().getId())
                < quantity) {
          throw new IllegalArgumentException("Not enough package units available");
        }
        break;
      case BOX:
        if (blindboxSeriesService.getAvailableBlindboxQuantityOfSeries(cartItem.getSeries().getId())
                < quantity) {
          throw new IllegalArgumentException("Not enough box units available");
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid product type");
    }
  }

  private void validateDiscountedUnitsAvailability(CartItem cartItem) {
    PreorderCampaign activeCampaign =
            preorderCampaignService
                    .getOngoingCampaignOfBlindboxSeries(cartItem.getSeries().getId())
                    .orElseThrow(() -> new IllegalArgumentException("No active campaign found"));

    switch (cartItem.getItemCampaignType()) {
      case GROUP:
      case null:
        break;
      case MILESTONE:
        // Get the active tier
        List<CampaignTier> tiers = campaignTierService.getCampaignTiersByCampaignId(activeCampaign.getId());
        Optional<CampaignTier> activeTierOptional = tiers.stream()
                .filter(tier -> tier.getTierStatus() == TierStatus.PROCESSING)
                .findFirst();

        if (activeTierOptional.isEmpty()) {
          throw new IllegalStateException("No active tier found for milestone campaign");
        }

        CampaignTier activeTier = activeTierOptional.get();
        int currentCount = activeTier.getCurrentCount();
        int thresholdQuantity = activeTier.getThresholdQuantity();
        int remainingCapacity = thresholdQuantity - currentCount;

        if (cartItem.getQuantity() > remainingCapacity) {
          throw new IllegalArgumentException("Not enough discounted units available. Only "
                  + remainingCapacity + " units remaining in the current tier.");
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid item campaign type");
    }
  }

  private void validateDiscountedUnitsAvailability(CartItem cartItem, int quantity) {
    PreorderCampaign activeCampaign =
            preorderCampaignService
                    .getOngoingCampaignOfBlindboxSeries(cartItem.getSeries().getId())
                    .orElseThrow(() -> new IllegalArgumentException("No active campaign found"));

    switch (cartItem.getItemCampaignType()) {
      case GROUP:
      case null:
        break;
      case MILESTONE:
        int availableDiscountedUnits =
                preorderCampaignService.getCurrentUnitsCountOfActiveTierOfOngoingCampaign(
                        activeCampaign.getId());
        if (availableDiscountedUnits < quantity) {
          throw new IllegalArgumentException("Not enough discounted units available");
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid item campaign type");
    }
  }
}
