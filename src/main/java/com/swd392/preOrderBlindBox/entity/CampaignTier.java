package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.common.enums.TierStatus;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "campaign_id", nullable = false)
  private PreorderCampaign campaign;

  @Column(length = 50)
  private String alias;

  @Column(name = "current_count", nullable = false)
  private Integer currentCount;

  @Column(name = "threshold_quantity", nullable = false)
  private Integer thresholdQuantity;

  @Column(name = "tier_order", nullable = false)
  private Integer tierOrder;

  @Column(name = "discount_percent", nullable = false)
  private Integer discountPercent;

  @Enumerated(EnumType.STRING)
  @Column(name = "tier_status", nullable = false)
  private TierStatus tierStatus;
}
