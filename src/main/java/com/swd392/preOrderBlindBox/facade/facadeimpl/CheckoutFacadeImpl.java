//package com.swd392.preOrderBlindBox.facade.facadeimpl;
//
//import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
//import com.swd392.preOrderBlindBox.common.enums.TransactionType;
//import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
//import com.swd392.preOrderBlindBox.entity.Preorder;
//import com.swd392.preOrderBlindBox.entity.Transaction;
//import com.swd392.preOrderBlindBox.facade.facade.CheckoutFacade;
//import com.swd392.preOrderBlindBox.service.service.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class CheckoutFacadeImpl implements CheckoutFacade {
//    private final UserService userService;
//    private final CartService cartService;
//    private final PreorderService preorderService;
//    private final TransactionService transactionService;
//    private final PaymentService paymentService;
//    private final BlindboxSeriesService blindboxSeriesService;
//
//    @Override
//    public Preorder createPreorder(Preorder preorder) {
//        if (preorder == null || preorder.getUser() == null) {
//            throw new IllegalArgumentException("Invalid preorder details");
//        }
//        return preorderService.createPreorder(preorder);
//    }
//
//    @Override
//    public String initiatePayment(Long preorderId, TransactionType transactionType, boolean isDeposit) {
//        Optional<Preorder> preorderOptional = preorderService.getPreorderById(preorderId);
//        if (preorderOptional.isEmpty()) {
//            throw new IllegalArgumentException("Preorder not found");
//        }
//        Preorder preorder = preorderOptional.get();
//
//        BigDecimal amount = preorder.getTotalPrice();
//        if (isDeposit) {
//            amount = amount.multiply(BigDecimal.valueOf(0.5));
//        }
//
//        Transaction transaction = transactionService.createTransaction(
//                preorderId,
//                transactionType,
//                amount,
//                isDeposit
//        );
//
//
//    }
//
//    @Override
//    public boolean finalizePayment(String transactionCode, boolean success) {
//        // Retrieve the transaction
//        Optional<Transaction> transactionOpt = transactionService.getTransactionByCode(transactionCode);
//        if (transactionOpt.isEmpty()) {
//            throw new IllegalArgumentException("Transaction not found");
//        }
//
//        Transaction transaction = transactionOpt.get();
//        TransactionStatus newStatus = success ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;
//
//        // Update transaction status
//        transactionService.updateTransactionStatus(transaction.getId(), newStatus);
//
//        // Handle preorder status update if payment is successful
//        if (success) {
//            preorderService.updatePreorderStatus(transaction.getPreorder().getId(), "PAYMENT_SUCCESSFUL");
//        }
//
//        return success;
//    }
//}
