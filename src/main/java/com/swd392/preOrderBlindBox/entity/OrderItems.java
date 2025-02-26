package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItems extends BaseEntity implements Serializable {
  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Orders order;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_item_id", nullable = false)
  private CartItem cartItem;

  @Column(name = "price", nullable = false, updatable = false) //since price of order_item should be locked and unchangeable
  private BigDecimal price;

  @Column(name = "quantity", nullable = false)
  private int quantity;
}
