package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.restcontroller.response.PaymentResponse;
import com.swd392.preOrderBlindBox.service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    public Preorder createPreorder(PreorderRequest preorderRequest) {
        Preorder preorder = modelMapper.map(preorderRequest, Preorder.class);
        return preorderService.createPreorder(preorder);
    }

    @Override
    public String initiatePayment(Long preorderId, TransactionType transactionType, boolean isDeposit) {
        Optional<Preorder> preorderOptional = preorderService.getPreorderById(preorderId);
        if (preorderOptional.isEmpty()) {
            throw new IllegalArgumentException("Preorder not found");
        }
        Preorder preorder = preorderOptional.get();

        BigDecimal amount = preorder.getTotalPrice();
//        if (isDeposit) {
//            amount = amount.multiply(BigDecimal.valueOf(0.5));
//        }

        Transaction transaction = transactionService.createTransaction(
                preorderId,
                transactionType,
                amount,
                isDeposit
        );

        return String.format(
                "/api/v1/payment/vn-pay?amount=%d&preorderId=%d&username=%s&transactionId=%d",
                amount.multiply(BigDecimal.valueOf(100)).longValue(),
                preorder.getId(),
                preorder.getUser().getEmail(),
                transaction.getId()
        );
    }

    @Override
    public String finalizePayment(Long transactionId, String transactionCode, Long preorderId, boolean success) {
        // Retrieve transaction and preorder
        Transaction transaction = transactionService.getTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Preorder preorder = preorderService.getPreorderById(preorderId)
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
        return paymentService.createVnPayPayment(request);
    }

    //Handles successful payment logic
    private String handleSuccessfulPayment(Transaction transaction, Preorder preorder) {
        transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.SUCCESS);
        preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.FULL_PAYMENT_SUCCESSFUL);

        // Assign blindbox products to preorder items
        preorderService.assignBlindboxProductToPreorderItem(preorder.getId());

        // Increment campaign units for each preorder item
        for (PreorderItem preorderItem : preorder.getPreorderItems()) {
            incrementCampaignUnits(preorderItem);
        }

        return "?paymentStatus=success";
    }

    //Handles failed payment logic
    private String handleFailedPayment(Transaction transaction, Preorder preorder) {
        transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.FAILED);
        preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.FULL_PAYMENT_FAILED);
        return "?paymentStatus=failed";
    }

    //Increments the units count in the corresponding campaign
    private void incrementCampaignUnits(PreorderItem preorderItem) {
        PreorderCampaign campaign = preorderCampaignService.getOngoingCampaignOfBlindboxSeries(preorderItem.getBlindboxSeries().getId())
                .orElseThrow(() -> new IllegalArgumentException("No active campaign found for this series"));

        preorderCampaignService.incrementUnitsCount(campaign.getId(), preorderItem.getQuantity());
    }

}
