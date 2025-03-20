package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    BaseResponse<PaymentResponse> createVnPayPayment(HttpServletRequest request);
}
