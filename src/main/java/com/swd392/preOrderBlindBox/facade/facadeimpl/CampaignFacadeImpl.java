package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.entity.CampaignTier;
import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import com.swd392.preOrderBlindBox.facade.facade.CampaignFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderCampaignRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.CampaignTierResponse;
import com.swd392.preOrderBlindBox.service.service.BlindboxSeriesService;
import com.swd392.preOrderBlindBox.service.service.PreorderCampaignService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignFacadeImpl implements CampaignFacade {

  private final PreorderCampaignService preorderCampaignService;
  private final BlindboxSeriesService blindboxSeriesService;

  @Override
  public BaseResponse<List<CampaignTierResponse>> getAllCampaignTiers(Long campaignId) {
    return null;
  }

  @Override
  public BaseResponse<CampaignTierResponse> getCampaignTierWithDetailsById(Long id) {
    return null;
  }

  @Override
  @Transactional
  public BaseResponse<Void> createCampaignTier(PreorderCampaignRequest request) {
    BlindboxSeries blindboxSeries =
        blindboxSeriesService.getBlindboxSeriesById(request.getBlindboxSeriesId());
    PreorderCampaign preorderCampaign =
        PreorderCampaign.builder()
            .campaignType(request.getCampaignType())
            .blindboxSeries(blindboxSeries)
            .campaignType(request.getCampaignType())
            .startCampaignTime(request.getStartCampaignTime())
            .endCampaignTime(request.getEndCampaignTime())
            .build();
    List<CampaignTier> campaignTiers =
        request.getCampaignTiers().stream()
            .map(
                campaignTier ->
                    CampaignTier.builder()
                        .campaign(preorderCampaign)
                        .alias(campaignTier.getAlias())
                        .currentCount(campaignTier.getCurrentCount())
                        .thresholdQuantity(campaignTier.getThresholdQuantity())
                        .discountPercent(campaignTier.getDiscountPercent())
                        .tierOrder(campaignTier.getTierOrder())
                        .tierStatus(campaignTier.getTierStatus())
                        .build())
            .toList();

    preorderCampaign.setCampaignTiers(campaignTiers);
    preorderCampaignService.createCampaign(preorderCampaign);
    return BaseResponse.ok();
  }
}
