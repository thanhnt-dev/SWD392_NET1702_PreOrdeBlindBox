package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.common.util.Util;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.*;
import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
import com.swd392.preOrderBlindBox.service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutFacadeImpl implements CheckoutFacade {
    private final PreorderService preorderService;
    private final TransactionService transactionService;
    private final PreorderCampaignService preorderCampaignService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final CartService cartService;
    @Value("${deposit.rate:0.5}")
    private BigDecimal depositRate;

    @Override
    public BaseResponse<PreorderEstimateResponse> getPreorderEstimate() {
        Cart cart = cartService.getOrCreateCart();
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        BigDecimal estimatedTotalAmount = cartService.calculateCartTotal();
        BigDecimal depositAmount = Util.calculatePriceWithCoefficient(estimatedTotalAmount, depositRate);
        BigDecimal remainingAmount = null;
        BigDecimal totalAmount = null;
        if (!hasCampaignItemOfTypeGroup(cart)) {
            totalAmount = estimatedTotalAmount;
            remainingAmount = Util.normalizePrice(totalAmount.subtract(depositAmount));
        }
        List<PreorderItemEstimateResponse> items = cart.getCartItems().stream()
                .map(item -> {
                    PreorderItemEstimateResponse responseItem = modelMapper.map(item, PreorderItemEstimateResponse.class);
                    responseItem.setDiscountedTotalPrice(cartService.calculateItemTotal(item));
                    return responseItem;
                })
                .toList();

        PreorderEstimateResponse response = new PreorderEstimateResponse();
        response.setEstimatedTotalAmount(estimatedTotalAmount);
        response.setTotalAmount(totalAmount);
        response.setDepositAmount(depositAmount);
        response.setRemainingAmount(remainingAmount);
        response.setItems(items);

        return BaseResponse.build(response, true);
    }

    private boolean hasCampaignItemOfTypeGroup(Cart cart) {
        return cart.getCartItems().stream()
                .anyMatch(item -> item.getItemCampaignType() == CampaignType.GROUP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> initiateDepositPayment(PreorderRequest preorderRequest, TransactionType transactionType) {
        Preorder preorder = modelMapper.map(preorderRequest, Preorder.class);
        Preorder savedPreorder = preorderService.createPreorder(preorder);

        BigDecimal amount = determinePaymentAmount(true, savedPreorder);
        Transaction transaction = transactionService.createTransaction(
                savedPreorder.getId(),
                transactionType,
                amount,
                true
        );

        String paymentUrl = String.format(
                "/api/v1/payment/vn-pay?amount=%d&preorderId=%d&username=%s&transactionId=%d",
                amount.multiply(BigDecimal.valueOf(100)).longValue(),
                savedPreorder.getId(),
                savedPreorder.getUser().getEmail(),
                transaction.getId()
        );
        return BaseResponse.build(paymentUrl, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> initiateRemainingAmountPayment(Long preorderId, TransactionType transactionType) {
        Preorder preorder = preorderService.getPreorderById(preorderId)
                .orElseThrow(() -> new IllegalArgumentException("Preorder not found"));

        BigDecimal amount = determinePaymentAmount(false, preorder);
        Transaction transaction = transactionService.createTransaction(
                preorder.getId(),
                transactionType,
                amount,
                false
        );

        String paymentUrl = String.format(
                "/api/v1/payment/vn-pay?amount=%d&preorderId=%d&username=%s&transactionId=%d",
                amount.multiply(BigDecimal.valueOf(100)).longValue(),
                preorder.getId(),
                preorder.getUser().getEmail(),
                transaction.getId()
        );
        return BaseResponse.build(paymentUrl, true);
    }

    private BigDecimal determinePaymentAmount(boolean isDeposit, Preorder preorder) {
        if (isDeposit) {
            if (preorder.getPreorderStatus() != PreorderStatus.PENDING) {
                throw new IllegalStateException("Deposit already paid or preorder not pending");
            }
            return preorder.getDepositAmount();
        } else {
            if (preorder.getPreorderStatus() != PreorderStatus.DEPOSIT_PAID) {
                throw new IllegalStateException("Deposit must be paid before remaining payment");
            }
            if (preorder.getRemainingAmount() == null) {
                throw new IllegalStateException("Remaining amount TBD due to GROUP campaign");
            }
            return preorder.getRemainingAmount();
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String finalizePayment(Long transactionId, String transactionCode, Long preorderId, boolean success) {
        // Retrieve transaction and preorder
        Transaction transaction = transactionService.getTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Preorder preorder = preorderService.getPreorderById(preorderId)
                .orElseThrow(() -> new IllegalArgumentException("Preorder not found"));

        // Assign transaction code received from VNPay
        transaction.setTransactionCode(transactionCode);

        if (success) {
            if (transaction.getIsDeposit()) {
                return handleSuccessDepositPayment(transaction, preorder);
            } else {
                return handleSuccessRemainingAmountPayment(transaction, preorder);
            }
        } else {
            if (transaction.getIsDeposit()) {
                return handleFailedDepositPayment(transaction, preorder);
            } else {
                return handleFailedRemainingAmountPayment(transaction, preorder);
            }
        }
    }

    @Override
    public BaseResponse<PaymentResponse> createVnPayPaymentRequest(HttpServletRequest request) {
        String preorderIdStr = request.getParameter("preorderId");
        String transactionIdStr = request.getParameter("transactionId");

        if (preorderIdStr == null || transactionIdStr == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }

        long preorderId, transactionId;
        try {
            preorderId = Long.parseLong(preorderIdStr);
            transactionId = Long.parseLong(transactionIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        Transaction transaction = transactionService.getTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (!transaction.getPreorder().getId().equals(preorderId)) {
            throw new IllegalArgumentException("Transaction does not match preorder");
        }

        if (transaction.getTransactionStatus() != TransactionStatus.PENDING) {
            throw new IllegalArgumentException("Transaction is not in PENDING status");
        }

        Preorder preorder = preorderService.getPreorderById(preorderId)
                .orElseThrow(() -> new IllegalArgumentException("Preorder not found"));


        if (preorder.getPreorderStatus() != PreorderStatus.PENDING && preorder.getPreorderStatus() != PreorderStatus.DEPOSIT_PAID) {
            throw new IllegalArgumentException("Invalid preorder status for payment");
        }

        return paymentService.createVnPayPayment(request);
    }

    private String handleSuccessDepositPayment(Transaction transaction, Preorder preorder) {
        transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.SUCCESS);
        preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.DEPOSIT_PAID);

        // Assign products to preorder items
        preorderService.assignBlindboxProductToPreorderItem(preorder.getId());

        // Handle campaign increments if applicable
        if (hasCampaignItems(preorder)) {
            incrementCampaignUnits(preorder);
        }

        return "?paymentStatus=success";
    }

    private String handleFailedDepositPayment(Transaction transaction, Preorder preorder) {
        transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.FAILED);
        preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.PENDING);
        return "?paymentStatus=failed";
    }

    private String handleSuccessRemainingAmountPayment(Transaction transaction, Preorder preorder) {
        transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.SUCCESS);
        preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.FULLY_PAID);
        return "?paymentStatus=success";
    }

    private String handleFailedRemainingAmountPayment(Transaction transaction, Preorder preorder) {
        transactionService.updateTransactionStatus(transaction.getId(), TransactionStatus.FAILED);
        preorderService.updatePreorderStatus(preorder.getId(), PreorderStatus.DEPOSIT_PAID);
        return "?paymentStatus=failed";
    }

    private boolean hasCampaignItems(Preorder preorder) {
        return preorder.getPreorderItems().stream()
                .anyMatch(item -> item.getItemFromCampaignType() != null);
    }

    //Increments the units count in the corresponding campaign
    private void incrementCampaignUnits(Preorder preorder) {
        Map<Long, Boolean> processedCampaigns = new HashMap<>(); // Track GROUP campaigns processed

        for (PreorderItem preorderItem : preorder.getPreorderItems()) {
            CampaignType campaignType = preorderItem.getItemFromCampaignType();
            if (campaignType == null) {
                continue; // Skip non-campaign items
            }

            PreorderCampaign ongoingCampaign = preorderCampaignService
                    .getOngoingCampaignOfBlindboxSeries(preorderItem.getBlindboxSeries().getId())
                    .orElseThrow(() -> new IllegalArgumentException("No ongoing campaign found"));

            if (campaignType == CampaignType.MILESTONE) {
                // Handle MILESTONE campaign (increment by item quantity)
                preorderCampaignService.incrementUnitsCount(ongoingCampaign.getId(), preorderItem.getQuantity());

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
