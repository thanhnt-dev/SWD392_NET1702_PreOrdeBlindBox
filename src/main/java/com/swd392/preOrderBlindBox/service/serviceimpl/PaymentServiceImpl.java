package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.util.VNPayUtil;
import com.swd392.preOrderBlindBox.infrastructure.config.VNPayConfig;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import com.swd392.preOrderBlindBox.service.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final VNPayConfig vnPayConfig;

  public BaseResponse<PaymentResponse> createVnPayPayment(HttpServletRequest request) {
    long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
    String bankCode = request.getParameter("bankCode");
    String preorderId = request.getParameter("preorderId");
    String username = request.getParameter("username");
    String transactionId = request.getParameter("transactionId");
    Map<String, String> vnpParamsMap;

    if (transactionId != null) {
      int id = Integer.parseInt(transactionId);
      vnpParamsMap = vnPayConfig.getVNPayConfig(preorderId, username, id);
    } else {
      vnpParamsMap = vnPayConfig.getVNPayConfig(preorderId, username, 0);
    }
    vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
    if (bankCode != null && !bankCode.isEmpty()) {
      vnpParamsMap.put("vnp_BankCode", bankCode);
    }
    vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

    // Build query URL
    String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
    String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
    String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
    queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
    String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + queryUrl;

    PaymentResponse paymentResponse = new PaymentResponse();
    paymentResponse.setPaymentUrl(paymentUrl);

    return BaseResponse.build(paymentResponse, true);
  }
}
