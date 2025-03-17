package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class VNPayController {
  private final CheckoutFacade checkoutFacade;

  @Value("${frontend.base-url}")
  private String frontendBaseUrl;

  @GetMapping("/vn-pay")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Initiate payment process",
      tags = {"Payment APIs"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Payment process initiated"),
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
  public void payCallbackHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("username") String username,
      @RequestParam("preorderId") Long preorderId,
      @RequestParam("transactionId") Long transactionId)
      throws IOException {
    String bankCode = request.getParameter("vnp_BankCode");
    String status = request.getParameter("vnp_ResponseCode");
    String transactionCode = request.getParameter("vnp_TransactionNo");
    String redirectUrl =
        handlePaymentCallback(preorderId, transactionId, status, transactionCode, bankCode);
    response.sendRedirect(redirectUrl);
  }

  private String handlePaymentCallback(
      Long preorderId, Long transactionId, String status, String transactionCode, String bankCode) {
    String redirectUrl = frontendBaseUrl + "/preorder/" + preorderId;
    boolean isSuccess = status.equals("00");

    return redirectUrl
        + checkoutFacade.finalizePayment(transactionId, transactionCode, preorderId, isSuccess);
  }
}
