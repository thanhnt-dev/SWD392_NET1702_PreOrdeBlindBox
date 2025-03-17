package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.Preorder;
import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/checkout")
@RequiredArgsConstructor
public class CheckoutController {

  private final CheckoutFacade checkoutFacade;

  @PostMapping
  @PreAuthorize("hasRole('USER')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Process checkout",
      tags = {"Checkout APIs"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Checkout process initiated"),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
      })
  public BaseResponse<String> processCheckout(@Valid @RequestBody PreorderRequest preorderRequest) {
    Preorder preorder = checkoutFacade.createPreorder(preorderRequest);

    if (preorder != null && preorder.getId() != null) {
      String paymentUrl =
          checkoutFacade.initiatePayment(preorder.getId(), TransactionType.VNPAY, false);
      return BaseResponse.build(paymentUrl, true);
    } else {
      return BaseResponse.build("Failed to create preorder", false);
    }
  }
}
