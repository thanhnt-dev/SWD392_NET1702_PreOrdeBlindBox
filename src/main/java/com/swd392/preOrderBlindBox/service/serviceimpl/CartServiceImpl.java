package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.repository.repository.CartItemRepository;
import com.swd392.preOrderBlindBox.repository.repository.CartRepository;
import com.swd392.preOrderBlindBox.service.service.BlindboxSeriesService;
import com.swd392.preOrderBlindBox.service.service.CartService;
import com.swd392.preOrderBlindBox.service.service.PreorderCampaignService;
import com.swd392.preOrderBlindBox.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final BlindboxSeriesService blindboxSeriesService;
    private final PreorderCampaignService preorderCampaignService;

    @Override
    @Transactional
    public Cart getOrCreateCart() {
        User currentUser = userService.getCurrentUser()
                .orElseThrow(() -> new SecurityException("User must be logged in to access the cart"));

        return cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(currentUser);
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

        CartItem existingItem = cartItemRepository.findByCartIdAndSeriesId(cart.getId(), cartItem.getSeries().getId()).orElse(null);
        PreorderCampaign ongoingCampaign = preorderCampaignService.getOngoingCampaignOfBlindboxSeries(cartItem.getSeries().getId()).orElse(null);

        validateProductAvailability(cartItem);

        if (ongoingCampaign != null) {
            validateDiscountedUnitsAvailability(cartItem);
            cartItem.setDiscountPercent(preorderCampaignService.getDiscountOfActiveTierOfOnGoingCampaign(ongoingCampaign.getId()));
        }

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
            if (newQuantity <= 0) {
                cartItemRepository.delete(existingItem);
                return null;
            }
            existingItem.setQuantity(newQuantity);
            return cartItemRepository.save(existingItem);
        }

        return cartItem.getQuantity() > 0 ? cartItemRepository.save(cartItem) : null;
    }



    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        verifyCartOwnership(cartItem);

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        verifyCartOwnership(cartItem);

        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && (cart.getUser() == null || !cart.getUser().getId().equals(currentUser.get().getId()))) {
            throw new SecurityException("Cannot clear cart that doesn't belong to current user");
        }

        cartItemRepository.deleteByCartId(cartId);
    }

    @Override
    public BigDecimal calculateCartTotal(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        verifyCartOwnership(cart);

        List<CartItem> cartItems = getCartItems(cartId);

        return cartItems.stream()
                .map(item -> {
                    BigDecimal price = item.getPrice();
                    int quantity = item.getQuantity();
                    BigDecimal discount = BigDecimal.valueOf(item.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    return price.multiply(BigDecimal.valueOf(quantity)).multiply(BigDecimal.ONE.subtract(discount));
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
        if (currentUser.isPresent() && (cart.getUser() == null || !cart.getUser().getId().equals(currentUser.get().getId()))) {
            throw new SecurityException(String.valueOf(ErrorCode.UNAUTHORIZED_CART_ACCESS));
        }
    }

    private void validateProductAvailability(CartItem cartItem) {
        switch (cartItem.getProductType()) {
            case PACKAGE:
                if (blindboxSeriesService.getAvailablePackageQuantityOfSeries(cartItem.getSeries().getId()) < cartItem.getQuantity()) {
                    throw new IllegalArgumentException("Not enough package units available");
                }
                break;
            case BOX:
                if (blindboxSeriesService.getAvailableBlindboxQuantityOfSeries(cartItem.getSeries().getId()) < cartItem.getQuantity()) {
                    throw new IllegalArgumentException("Not enough box units available");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid product type");
        }
    }

    private void validateDiscountedUnitsAvailability(CartItem cartItem) {
        PreorderCampaign activeCampaign = preorderCampaignService.getOngoingCampaignOfBlindboxSeries(cartItem.getSeries().getId())
                .orElseThrow(() -> new IllegalArgumentException("No active campaign found"));


        switch (cartItem.getItemCampaignType()) {
            case GROUP:
                break;
            case MILESTONE:
                validateProductAvailability(cartItem);
                int availableDiscountedUnits = preorderCampaignService.getCurrentUnitsCountOfActiveTierOfOngoingCampaign(activeCampaign.getId());
                if (cartItem.getQuantity() > availableDiscountedUnits) {
                    throw new IllegalArgumentException("Not enough discounted units available");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid item campaign type");
        }
    }
}