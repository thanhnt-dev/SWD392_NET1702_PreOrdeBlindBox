package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.enums.CampaignType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign extends BaseEntity implements Serializable {
  @Column(name = "campaign_type")
  @Enumerated(EnumType.STRING)
  private CampaignType campaignType;

  @Column(name = "start_campaign_time", nullable = false)
  private LocalDateTime startCampaignTime;

  @Column(name = "end_campaign_time", nullable = false)
  private LocalDateTime endCampaignTime;

  @Column(name = "current_placed_blindbox", nullable = false)
  private int currentPlacedBlindbox;

  @Column(name = "target_blindbox_quantity", nullable = false)
  private int targetBlindboxQuantity;

  @Column(name = "deposit_percent", nullable = true)
  private int depositPercent;

  @Column(name = "base_price", nullable = false)
  private BigDecimal basePrice;

  @ManyToOne(
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "blindbox_series_id", nullable = false)
  private BlindboxSeries blindboxSeries;
}
