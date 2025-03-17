package com.swd392.preOrderBlindBox.facade.facade;


import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.Preorder;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderEstimateResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface CheckoutFacade {
    BaseResponse<String> initiateDepositPayment(PreorderRequest preorderRequest, TransactionType transactionType);

    BaseResponse<String> initiateRemainingAmountPayment(Long preorderId, TransactionType transactionType);

    BaseResponse<PreorderEstimateResponse> getPreorderEstimate();

    String finalizePayment(Long transactionId, String transactionCode, Long preorderId, boolean success);

    BaseResponse<PaymentResponse> createVnPayPaymentRequest(HttpServletRequest request);

}
