package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.enums.TransactionStatus;
import com.swd392.preOrderBlindBox.enums.TransactionType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity implements Serializable {

  @ManyToOne(
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Orders order;

  @Column(name = "transaction_code", nullable = false, unique = true, length = 20)
  private String transactionCode;

  @Column(name = "transaction_type", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  @Column(name = "content", nullable = true)
  private String content;

  @Column(name = "transaction_amount", nullable = false)
  private BigDecimal transactionAmount;

  @Column(name = "transaction_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private TransactionStatus transactionStatus;

  @Column(name = "is_deposit", nullable = false)
  @Builder.Default
  private Boolean isDeposit = false;

  @ManyToOne
  @JoinColumn(name = "related_transaction_id")
  private Transaction relatedTransaction;
}
