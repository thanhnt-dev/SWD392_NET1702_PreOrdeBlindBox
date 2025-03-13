//package com.swd392.preOrderBlindBox.restcontroller.controller;
//
//import com.swd392.preOrderBlindBox.service.service.PaymentService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/payment")
//@RequiredArgsConstructor
//public class VNPayController {
//
//    private final PaymentService paymentService;
//    private final TransactionService transactionService;
//
//    @GetMapping("/vn-pay")
//    public ResponseObject<PaymentResponse.VNPayResponse> pay(HttpServletRequest request) {
//        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
//    }
//
//    @GetMapping("/vn-pay-callback")
//    public void payCallbackHandler(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestParam("username") String username,
//            @RequestParam("auctionId") Integer auctionId,
//            @RequestParam("transactionId") Integer transactionId
//    ) throws IOException {
//        String bankCode = request.getParameter("vnp_BankCode");
//        String status = request.getParameter("vnp_ResponseCode");
//        String transactionCode = request.getParameter("vnp_TransactionNo");
//        String redirectUrl = "";
//
//        if (transactionId == 0) {
//            redirectUrl = handleAuctionPaymentCallback(username, auctionId, status, transactionCode, bankCode);
//        } else {
//            redirectUrl = handleTransactionPaymentCallback(transactionId, status, transactionCode, bankCode);
//        }
//        response.sendRedirect(redirectUrl);
//    }
//
//    private String handleAuctionPaymentCallback(String username, Integer auctionId, String status, String transactionCode, String bankCode) {
//        String baseUrl = frontendConfiguration.getBaseUrl() + "/tai-san-dau-gia/";
//        String redirectUrl = baseUrl + auctionId;
//
//        if (!status.equals("00")) {
//            redirectUrl += "?paymentStatus=failed";
//        } else {
//            auctionRegistrationService.registerUserForAuction(username, auctionId, transactionCode, bankCode);
//            redirectUrl += "?paymentStatus=success";
//        }
//
//        return redirectUrl;
//    }
//
//    private String handleTransactionPaymentCallback(Integer transactionId, String status, String transactionCode, String bankCode) {
//        String redirectUrl = frontendConfiguration.getBaseUrl() + "/thong-tin-ca-nhan/";
//
//        if (!status.equals("00")) {
//            redirectUrl += "?paymentStatus=failed";
//        } else {
//            transactionService.setTransactionAfterPaySuccess(transactionId, transactionCode, bankCode);
//            redirectUrl += "?paymentStatus=success";
//        }
//
//        return redirectUrl;
//    }
//
//}