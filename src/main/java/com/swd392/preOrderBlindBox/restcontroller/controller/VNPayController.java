package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.common.enums.Platform;
import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class VNPayController {
    private final CheckoutFacade checkoutFacade;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${mobile.base-url}")
    private String mobileBaseUrl;

    @GetMapping("/vn-pay")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Initiate payment process",
            tags = {"Payment APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Payment process initiated"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<PaymentResponse> pay(HttpServletRequest request) {
        return checkoutFacade.createVnPayPaymentRequest(request);
    }

    @GetMapping("/vn-pay-callback")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Internal server-to-server callback handler for VNPay",
            tags = {"Payment APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Payment callback handled"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public String payCallbackHandler(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        try {
            // Extract parameters manually from request to avoid Spring's binding mechanisms
            String username = request.getParameter("username");
            Long preorderId = Long.valueOf(request.getParameter("preorderId"));
            Long transactionId = Long.valueOf(request.getParameter("transactionId"));
            String platformStr = request.getParameter("platform");

            // Safely convert platform string to enum
            Platform platform = Platform.WEB; // Default value
            if (platformStr != null && !platformStr.isEmpty()) {
                try {
                    platform = Platform.valueOf(platformStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid platform value: " + platformStr + ". Using WEB as default.");
                }
            }

            String bankCode = request.getParameter("vnp_BankCode");
            String status = request.getParameter("vnp_ResponseCode");
            String transactionCode = request.getParameter("vnp_TransactionNo");

            String redirectUrl = handlePaymentCallback(preorderId, transactionId, status, transactionCode, bankCode, platform);
            response.sendRedirect(redirectUrl);
            return "Redirecting to: " + redirectUrl;
        } catch (Exception e) {
            e.printStackTrace();
            String errorRedirect = frontendBaseUrl + "/payment-error?message=" + e.getMessage();
            response.sendRedirect(errorRedirect);
            return "Error occurred: " + e.getMessage();
        }
    }

    private String handlePaymentCallback(Long preorderId, Long transactionId, String status,
                                         String transactionCode, String bankCode, Platform platform) {
        String redirectUrl;

        if (platform == Platform.MOBILE) {
            redirectUrl = mobileBaseUrl + "account/preorders/" + preorderId;
        } else {
            redirectUrl = frontendBaseUrl + "account/preorders/" + preorderId;
        }

        boolean isSuccess = status.equals("00");
        return redirectUrl + checkoutFacade.finalizePayment(transactionId, transactionCode, preorderId, isSuccess);
    }

}