package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
  Transaction createTransaction(
      Long preorderId, TransactionType transactionType, BigDecimal amount, boolean isDeposit);

  void updateTransactionStatus(Long id, TransactionStatus status);

  List<Transaction> getTransactionsOfPreorder(Long preorderId);

  Optional<Transaction> getTransactionById(Long id);
}
