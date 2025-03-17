package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import com.swd392.preOrderBlindBox.service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutFacadeImpl implements CheckoutFacade {
  private final PreorderService preorderService;
  private final TransactionService transactionService;
  private final PreorderCampaignService preorderCampaignService;
  private final ModelMapper modelMapper;
  private final PaymentService paymentService;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Preorder createPreorder(PreorderRequest preorderRequest) {
    Preorder preorder = modelMapper.map(preorderRequest, Preorder.class);
    return preorderService.createPreorder(preorder);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String initiatePayment(
      Long preorderId, TransactionType transactionType, boolean isDeposit) {
    Optional<Preorder> preorderOptional = preorderService.getPreorderById(preorderId);
    if (preorderOptional.isEmpty()) {
      throw new IllegalArgumentException("Preorder not found");
    }
    Preorder preorder = preorderOptional.get();

    BigDecimal amount = preorder.getTotalPrice();
    //        if (isDeposit) {
    //            amount = amount.multiply(BigDecimal.valueOf(0.5));
    //        }

    Transaction transaction =
        transactionService.createTransaction(preorderId, transactionType, amount, isDeposit);

    return String.format(
        "/api/v1/payment/vn-pay?amount=%d&preorderId=%d&username=%s&transactionId=%d",
        amount.multiply(BigDecimal.valueOf(100)).longValue(),
        preorder.getId(),
        preorder.getUser().getEmail(),
        transaction.getId());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String finalizePayment(
      Long transactionId, String transactionCode, Long preorderId, boolean success) {
    // Retrieve transaction and preorder
    Transaction transaction =
        transactionService
            .getTransactionById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

    Preorder preorder =
        preorderService
            .getPreorderById(preorderId)
            .orElseThrow(() -> new IllegalArgumentException("Preorder not found"));

    // Assign transaction code received from VNPay
    transaction.setTransactionCode(transactionCode);

    if (success) {
      return handleSuccessfulPayment(transaction, preorder);
    } else {
      return handleFailedPayment(transaction, preorder);
    }
  }

  @Override
  public BaseResponse<PaymentResponse> createVnPayPaymentRequest(HttpServletRequest request) {
    String preorderId = request.getParameter("preorderId");
    String transactionId = request.getParameter("transactionId");
    if (preorderId == null || transactionId == null) {
      throw new IllegalArgumentException("Preorder ID not found");
    }

    return paymentService.createVnPayPayment(request);
  }

  // Handles successful payment logic
  private String handleSuccessfulPayment(Transaction transaction, Preorder preorder) {
    transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.SUCCESS);
    preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.FULL_PAYMENT_SUCCESSFUL);

    // Assign products to preorder items
    preorderService.assignBlindboxProductToPreorderItem(preorder.getId());

    // Handle campaign increments if applicable
    if (hasCampaignItems(preorder)) {
      incrementCampaignUnits(preorder);
    }

    return "?paymentStatus=success";
  }

  // Handles failed payment logic
  private String handleFailedPayment(Transaction transaction, Preorder preorder) {
    transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.FAILED);
    preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.FULL_PAYMENT_FAILED);
    return "?paymentStatus=failed";
  }

  private boolean hasCampaignItems(Preorder preorder) {
    return preorder.getPreorderItems().stream()
        .anyMatch(item -> item.getItemFromCampaignType() != null);
  }

  // Increments the units count in the corresponding campaign
  private void incrementCampaignUnits(Preorder preorder) {
    Map<Long, Boolean> processedCampaigns = new HashMap<>(); // Track GROUP campaigns processed

    for (PreorderItem preorderItem : preorder.getPreorderItems()) {
      CampaignType campaignType = preorderItem.getItemFromCampaignType();
      if (campaignType == null) {
        continue; // Skip non-campaign items
      }

      PreorderCampaign ongoingCampaign =
          preorderCampaignService
              .getOngoingCampaignOfBlindboxSeries(preorderItem.getBlindboxSeries().getId())
              .orElseThrow(() -> new IllegalArgumentException("No ongoing campaign found"));

      if (campaignType == CampaignType.MILESTONE) {
        // Handle MILESTONE campaign (increment by item quantity)
        preorderCampaignService.incrementUnitsCount(
            ongoingCampaign.getId(), preorderItem.getQuantity());

      } else if (campaignType == CampaignType.GROUP) {
        // Handle GROUP campaign (increment only once per preorder)
        // Ensure the campaign is counted only once per preorder
        if (!processedCampaigns.containsKey(ongoingCampaign.getId())) {
          preorderCampaignService.incrementUnitsCount(ongoingCampaign.getId(), 1);
          processedCampaigns.put(ongoingCampaign.getId(), true);
        }
      }
    }
  }
}
