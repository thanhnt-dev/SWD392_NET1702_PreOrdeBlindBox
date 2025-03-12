package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreorderCampaign extends BaseEntity implements Serializable {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blindbox_series_id", nullable = false)
  private BlindboxSeries blindboxSeries;

  @Enumerated(EnumType.STRING)
  @Column(name = "campaign_type", nullable = false)
  private CampaignType campaignType;

  @Column(name = "start_campaign_time", nullable = false)
  private LocalDateTime startCampaignTime;

  @Column(name = "end_campaign_time", nullable = false)
  private LocalDateTime endCampaignTime;

  @OneToMany(mappedBy = "campaign")
  private List<CampaignTier> campaignTiers;
}
