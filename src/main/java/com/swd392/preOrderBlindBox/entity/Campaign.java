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
  private Integer depositPercent;

  @Column(name = "base_price", nullable = false)
  private BigDecimal basePrice;

  @Column(name = "locked_price", nullable = true)
  private BigDecimal lockedPrice;

  @ManyToOne(
          cascade = {CascadeType.ALL},
          fetch = FetchType.LAZY)
  @JoinColumn(name = "blindbox_series_id", nullable = false)
  private BlindboxSeries blindboxSeries;

  /**
   * Calculates the effective price based on tier discount
   * @param discountPercent the discount percentage from the tier
   * @return the calculated price after discount
   */
  public BigDecimal calculateEffectivePrice(int discountPercent) {
    if (discountPercent <= 0) {
      return basePrice;
    }

    BigDecimal discount = basePrice.multiply(
            BigDecimal.valueOf(discountPercent)
                    .divide(BigDecimal.valueOf(100)));

    return basePrice.subtract(discount);
  }
}