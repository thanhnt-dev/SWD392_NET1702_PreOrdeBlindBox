package com.swd392.preOrderBlindBox.restcontroller.request;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreorderCampaignRequest {
  @NotNull(message = "Blindbox Series ID không được để trống")
  private Long blindboxSeriesId;

  @NotNull(message = "Campaign Type không được để trống")
  private CampaignType campaignType;

  @NotNull(message = "Thời gian bắt đầu không được để trống")
  @Future(message = "Thời gian bắt đầu phải ở tương lai")
  private LocalDateTime startCampaignTime;

  @NotNull(message = "Thời gian kết thúc không được để trống")
  @Future(message = "Thời gian kết thúc phải ở tương lai")
  private LocalDateTime endCampaignTime;

  private List<CampaignTierRequest> campaignTiers;
}
