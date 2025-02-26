package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "blindbox_unit_id", nullable = false)
    private BlindboxUnit blindboxUnit;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_percent", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer discountPercent;
}