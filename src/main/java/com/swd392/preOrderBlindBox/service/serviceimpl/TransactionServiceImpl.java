package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.Preorder;
import com.swd392.preOrderBlindBox.entity.Transaction;
import com.swd392.preOrderBlindBox.repository.repository.TransactionRepository;
import com.swd392.preOrderBlindBox.service.service.PreorderService;
import com.swd392.preOrderBlindBox.service.service.TransactionService;
import com.swd392.preOrderBlindBox.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final PreorderService preorderService;

    @Override
    public Transaction createTransaction(Long preorderId, TransactionType transactionType, BigDecimal amount, boolean isDeposit) {
        Preorder preorder = preorderService.getPreorderById(preorderId)
                .orElseThrow(() -> new IllegalArgumentException("Preorder not found"));;

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        Transaction transaction = Transaction.builder()
                .preorder(preorder)
                .isDeposit(isDeposit)
                .transactionAmount(amount)
                .transactionType(transactionType)
                .user(userService.getCurrentUser().orElse(null))
                .transactionStatus(TransactionStatus.PENDING)
                .content(preorder.getOrderCode())
                .build();
        if (!isDeposit) {
            Transaction existingTransaction = transactionRepository.findByPreorderId(preorderId).stream()
                    .filter(t -> t.getTransactionStatus() == TransactionStatus.SUCCESS)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Deposit transaction not found"));
            transaction.setRelatedTransaction(existingTransaction);
        }

        return transactionRepository.save(transaction);
    }

    @Override
    public void updateTransactionStatus(Long id, TransactionStatus status) {
        transactionRepository.findById(id)
                .ifPresent(transaction -> {
                    transaction.setTransactionStatus(status);
                    transactionRepository.save(transaction);
                });
    }

    @Override
    public List<Transaction> getTransactionsOfPreorder(Long preorderId) {
        return transactionRepository.findByPreorderId(preorderId);
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
}
