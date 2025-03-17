package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.entity.Cart;
import com.swd392.preOrderBlindBox.entity.CartItem;
import com.swd392.preOrderBlindBox.restcontroller.request.CartItemRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.CartItemResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.CartResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;
import com.swd392.preOrderBlindBox.service.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/api/${api.version}/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
  private final CartService cartService;
  private final ModelMapper mapper;

  @GetMapping
  @PreAuthorize("hasRole('USER')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get cart of current user, or create new cart if not exist",
      tags = {"Cart APIs"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
      })
  public BaseResponse<CartResponse> getUserCart() {
    CartResponse cartResponse = mapper.map(cartService.getOrCreateCart(), CartResponse.class);
    calculateItemDiscountedPrices(cartResponse);
    return BaseResponse.build(cartResponse, true);
  }

  @PostMapping
  @PreAuthorize("hasRole('USER')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Add an item to cart",
      tags = {"Cart APIs"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Item added to cart successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Available quantity is insufficient",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Available discounted quantity is insufficient",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Resource not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
      })
  public BaseResponse<CartResponse> addItemsToCart(
      @Valid @RequestBody CartItemRequest cartItemRequest) {
    CartResponse cartResponse = mapper.map(cartService.addToCart(cartItemRequest), CartResponse.class);
    calculateItemDiscountedPrices(cartResponse);
    return BaseResponse.build(cartResponse, true);
  }

  @PutMapping("/{cartItemId}")
  @PreAuthorize("hasRole('USER')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Update quantity of an item in cart",
      tags = {"Cart APIs"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Cart item updated successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid quantity or request",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized access to cart",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cart item not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
      })
  public BaseResponse<CartResponse> updateCartItemQuantity(
      @PathVariable @NotNull Long cartItemId,
      @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
    CartResponse cartResponse = mapper.map(cartService.updateCartItemQuantity(cartItemId, quantity), CartResponse.class);
    calculateItemDiscountedPrices(cartResponse);
    return BaseResponse.build(cartResponse, true);
  }

  @DeleteMapping("/{cartItemId}")
  @PreAuthorize("hasRole('USER')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Remove an item from cart",
      tags = {"Cart APIs"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Cart item removed successfully"),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized access to cart",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cart item not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
      })
  public BaseResponse<CartResponse> removeCartItem(@PathVariable @NotNull Long cartItemId) {
    CartResponse cartResponse = mapper.map(cartService.removeCartItem(cartItemId), CartResponse.class);
    calculateItemDiscountedPrices(cartResponse);
    return BaseResponse.build(cartResponse, true);
  }

  @DeleteMapping
  @PreAuthorize("hasRole('USER')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Clear all items from cart",
      tags = {"Cart APIs"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized access to cart",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cart not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
      })
  public BaseResponse<CartResponse> clearCart() {
    return BaseResponse.build(mapper.map(cartService.clearCart(), CartResponse.class), true);
  }

  private void calculateItemDiscountedPrices(CartResponse cart) {
    List<CartItemResponse> cartItems = cart.getCartItems();
    if (cartItems != null) {
      for (CartItemResponse item : cartItems) {
        item.setDiscountedPrice(cartService.calculateItemDiscountedPrice(item));
      }
    }
  }

}
