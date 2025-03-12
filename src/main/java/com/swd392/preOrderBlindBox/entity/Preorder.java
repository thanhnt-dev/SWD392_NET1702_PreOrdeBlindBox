package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Preorder extends BaseEntity implements Serializable {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "order_code", nullable = false, length = 20)
  private String orderCode;

  @Column(name = "delivery_code", length = 20)
  private String deliveryCode;

  @Column(name = "user_address", length = 200)
  private String userAddress;

  @Column(name = "phone_number", length = 15)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "preorder_status")
  private PreorderStatus preorderStatus;

  @Column(name = "total_price", precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @OneToMany(mappedBy = "preorder")
  private List<PreorderItem> preorderItems;

  @OneToMany(mappedBy = "preorder")
  private List<Transaction> transactions;

}
