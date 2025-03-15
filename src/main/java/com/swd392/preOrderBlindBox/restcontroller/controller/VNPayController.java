package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import com.swd392.preOrderBlindBox.service.service.PaymentService;
import com.swd392.preOrderBlindBox.service.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class VNPayController {
    private final CheckoutFacade checkoutFacade;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @GetMapping("/vn-pay")
    public BaseResponse<PaymentResponse> pay(HttpServletRequest request) {
        return checkoutFacade.createVnPayPaymentRequest(request);
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("username") String username,
            @RequestParam("preorderId") Long preorderId,
            @RequestParam("transactionId") Long transactionId
    ) throws IOException {
        String bankCode = request.getParameter("vnp_BankCode");
        String status = request.getParameter("vnp_ResponseCode");
        String transactionCode = request.getParameter("vnp_TransactionNo");
        String redirectUrl = handlePaymentCallback(preorderId, transactionId, status, transactionCode, bankCode);
        response.sendRedirect(redirectUrl);
    }

    private String handlePaymentCallback(Long preorderId, Long transactionId, String status, String transactionCode, String bankCode) {
        String redirectUrl = frontendBaseUrl + "/preorder/" + preorderId;
        boolean isSuccess = status.equals("00");

        return redirectUrl + checkoutFacade.finalizePayment(transactionId, transactionCode, preorderId, isSuccess);
    }

}