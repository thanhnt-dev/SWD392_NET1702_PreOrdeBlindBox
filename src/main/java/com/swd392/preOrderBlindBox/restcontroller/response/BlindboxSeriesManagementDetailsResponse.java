package com.swd392.preOrderBlindBox.restcontroller.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxSeriesManagementDetailsResponse {
  private Long id;
  private String seriesName;
  private String description;
  private BigDecimal packagePrice;
  private BigDecimal boxPrice;
  private boolean isActive;
  private List<BlindboxAssetResponse> assets;
  private List<BlindboxPackageResponse> packages;
  private List<BlindboxSeriesItemResponse> items;
  private List<PreorderCampaignResponse> campaigns;
}
