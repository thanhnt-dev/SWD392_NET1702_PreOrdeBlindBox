package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.enums.OrderStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders extends BaseEntity implements Serializable {
  @Column(name = "order_code", nullable = false, unique = true, length = 20)
  public String orderCode;

  @Column(name = "delivery_code", unique = true, length = 20)
  public String deliveryCode;

  @Column(name = "user_address", nullable = false)
  public String userAddress;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Column(name = "total_price", nullable = false)
  private BigDecimal totalPrice;

  @Column(name = "is_preorder")
  @Builder.Default
  private boolean isPreorder = false;

  @ManyToOne(
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "campaign_id", nullable = false)
  private Campaign campaign;

  @ManyToOne(
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<OrderItems> orderItems = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Transaction> transactions = new ArrayList<>();
}
