package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blindbox_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxUnit extends BaseEntity implements Serializable {
    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "quantity_per_package", nullable = false)
    private int quantityPerPackage;

    @Column(name = "stock_quantity", nullable = true)
    private int stockQuantity;

    @Column(name = "price", nullable = true)
    private BigDecimal price;

    @Column(name = "discount_percent", nullable = true)
    private int discountPercent;

    @ManyToOne(
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "blindbox_series_id", nullable = false)
    private BlindboxSeries blindboxSeries;

    @OneToMany(mappedBy = "blindboxUnit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItems> orderItems = new ArrayList<>();
}
