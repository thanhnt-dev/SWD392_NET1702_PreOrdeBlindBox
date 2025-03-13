package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderCampaignDetailsResponse {
  private CampaignType campaignType;
  private LocalDateTime startCampaignTime;
  private LocalDateTime endCampaignTime;
  private boolean isActive;
  private int totalDiscountedUnits;
  private int currentUnitsCount;
  private List<CampaignTierResponse> campaignTiers;
}
