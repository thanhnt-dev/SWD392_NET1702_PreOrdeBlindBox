package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.entity.CartItem;
import com.swd392.preOrderBlindBox.restcontroller.request.CartItemRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.CartResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;
import com.swd392.preOrderBlindBox.service.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
  private final CartService cartService;
  private final ModelMapper mapper;

  @GetMapping
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
    return BaseResponse.build(mapper.map(cartService.getOrCreateCart(), CartResponse.class), true);
  }

  @PostMapping
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
    CartItem cartItem = mapper.map(cartItemRequest, CartItem.class);
    cartService.addToCart(cartItem);
    return getUpdatedCartResponse();
  }

  @PutMapping("/{cartItemId}")
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

    cartService.updateCartItemQuantity(cartItemId, quantity);
    return getUpdatedCartResponse();
  }

  @DeleteMapping("/{cartItemId}")
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
    cartService.removeCartItem(cartItemId);
    return getUpdatedCartResponse();
  }

  @DeleteMapping
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
    CartResponse cartResponse = mapper.map(cartService.getOrCreateCart(), CartResponse.class);
    cartService.clearCart(cartResponse.getId());
    return getUpdatedCartResponse();
  }

  // Helper method to get updated cart response with total price
  private BaseResponse<CartResponse> getUpdatedCartResponse() {
    CartResponse cartResponse = mapper.map(cartService.getOrCreateCart(), CartResponse.class);
    cartResponse.setTotalPrice(cartService.calculateCartTotal(cartResponse.getId()));
    return BaseResponse.build(cartResponse, true);
  }
}
