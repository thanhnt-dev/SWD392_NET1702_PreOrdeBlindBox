package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.PaymentProcessRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderEstimateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/${api.version}/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutFacade checkoutFacade;

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Process deposit checkout",
            tags = {"Checkout APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Checkout process initiated"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access to cart",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<String> processDepositCheckout(@Valid @RequestBody PreorderRequest preorderRequest) {
        return checkoutFacade.initiateDepositPayment(preorderRequest, TransactionType.VNPAY);
    }

    @PostMapping("/retry")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Retry a failed checkout transaction",
            tags = {"Checkout APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Checkout reprocessing initiated"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access to preorder",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Preorder not found",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<String> reprocessCheckout(@Valid @RequestBody PaymentProcessRequest request) {
        return checkoutFacade.reprocessPayment(request.getPreorderId(), TransactionType.VNPAY, request.getPlatform());
    }

    @PostMapping("/remaining-amount")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Process checkout for remaining amount of preorder",
            tags = {"Checkout APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Checkout process initiated"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access to cart",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<String> processRemainingAmountCheckout(@Valid @RequestBody PaymentProcessRequest request) {
        return checkoutFacade.initiateRemainingAmountPayment(request.getPreorderId(), TransactionType.VNPAY, request.getPlatform());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get checkout information (including estimate information for preorder)",
            tags = {"Checkout APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Information retrieved successfully"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access to cart",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<PreorderEstimateResponse> getCheckoutInformation() {
        return checkoutFacade.getPreorderEstimate();
    }
}