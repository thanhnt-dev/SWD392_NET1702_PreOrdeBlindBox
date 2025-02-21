package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Entity
@Table(name = "campaign_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignTier extends BaseEntity implements Serializable {

  @Column(name = "tier_name", nullable = false, length = 50)
  private String tierName;

  @Column(name = "min_quantity", nullable = false)
  private int minQuantity;

  @Column(name = "max_quantity", nullable = false)
  private int maxQuantity;

  @Column(name = "discount_percent", nullable = false)
  private int discountPercent;

  @ManyToOne(
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "campaign_id", nullable = false)
  private Campaign campaign;
}
