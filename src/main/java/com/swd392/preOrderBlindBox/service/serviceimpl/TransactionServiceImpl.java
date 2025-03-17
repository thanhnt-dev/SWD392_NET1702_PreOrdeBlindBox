package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.Preorder;
import com.swd392.preOrderBlindBox.entity.Transaction;
import com.swd392.preOrderBlindBox.repository.repository.TransactionRepository;
import com.swd392.preOrderBlindBox.service.service.PreorderService;
import com.swd392.preOrderBlindBox.service.service.TransactionService;
import com.swd392.preOrderBlindBox.service.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {
  private final TransactionRepository transactionRepository;
  private final UserService userService;
  private final PreorderService preorderService;

  @Override
  public Transaction createTransaction(
      Long preorderId, TransactionType transactionType, BigDecimal amount, boolean isDeposit) {
    Preorder preorder = preorderService.getPreorderById(preorderId).orElse(null);

    Transaction transaction =
        Transaction.builder()
            .preorder(preorder)
            .isDeposit(isDeposit)
            .transactionAmount(amount)
            .transactionType(transactionType)
            .user(userService.getCurrentUser().orElse(null))
            .transactionStatus(TransactionStatus.PENDING)
            .content(preorder != null ? preorder.getOrderCode() : null)
            .build();

    return transactionRepository.save(transaction);
  }

  @Override
  public void updateTransactionStatus(Long id, TransactionStatus status) {
    transactionRepository
        .findById(id)
        .ifPresent(
            transaction -> {
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

  //    private String generateTransactionCode() {
  //        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  //        StringBuilder code = new StringBuilder();
  //
  //        code.append("TX");
  //
  //        String timestamp = String.valueOf(System.currentTimeMillis());
  //        code.append(timestamp.substring(Math.max(0, timestamp.length() - 8)));
  //
  //        SecureRandom random = new SecureRandom();
  //        while (code.length() < 20) {
  //            code.append(alphanumeric.charAt(random.nextInt(alphanumeric.length())));
  //        }
  //
  //        return code.toString();
  //    }
}
