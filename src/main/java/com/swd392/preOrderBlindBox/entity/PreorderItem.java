package com.swd392.preOrderBlindBox.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.ProductType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "preorder_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreorderItem extends BaseEntity implements Serializable {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "preorder_id", nullable = false)
  @JsonBackReference
  private Preorder preorder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blindbox_series_id", nullable = false)
  private BlindboxSeries blindboxSeries;

  @Column(name = "product_id")
  private String productIds;

  @Enumerated(EnumType.STRING)
  @Column(name = "product_type", nullable = false)
  private ProductType productType;

  @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal originalPrice;

  @Column(name = "locked_price", precision = 10, scale = 2)
  private BigDecimal lockedPrice;

  @Column(nullable = false)
  private Integer quantity;

  @Enumerated(EnumType.STRING)
  @Column(name = "item_from_campaign_type")
  private CampaignType itemFromCampaignType;
}
