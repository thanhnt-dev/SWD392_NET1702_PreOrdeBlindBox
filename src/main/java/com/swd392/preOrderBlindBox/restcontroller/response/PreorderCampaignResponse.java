package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import java.time.LocalDateTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderCampaignResponse {
  Long id;
  BlindboxSeriesResponse series;
  CampaignType campaignType;
  LocalDateTime startCampaignTime;
  LocalDateTime endCampaignTime;
  boolean isActive;
  int totalDiscountedUnits;
}
