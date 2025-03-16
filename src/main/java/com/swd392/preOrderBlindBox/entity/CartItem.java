package com.swd392.preOrderBlindBox.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.ProductType;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  @JsonBackReference
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "series_id", nullable = false)
  private BlindboxSeries series;

  @Enumerated(EnumType.STRING)
  @Column(name = "product_type", nullable = false)
  private ProductType productType;

  @Column(nullable = false, columnDefinition = "int default 1")
  private Integer quantity = 1;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "discount_percent", nullable = false, columnDefinition = "int default 0")
  private Integer discountPercent = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "item_campaign_type", nullable = true)
  private CampaignType itemCampaignType;
}
