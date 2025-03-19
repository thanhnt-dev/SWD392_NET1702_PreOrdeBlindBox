package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.TierStatus;
import com.swd392.preOrderBlindBox.common.util.Util;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.facade.facade.CampaignFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderCampaignRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.CampaignTierResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderCampaignDetailsManagementResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderCampaignManagementResponse;
import com.swd392.preOrderBlindBox.service.service.BlindboxSeriesService;
import com.swd392.preOrderBlindBox.service.service.CampaignTierService;
import com.swd392.preOrderBlindBox.service.service.PreorderCampaignService;
import com.swd392.preOrderBlindBox.service.service.PreorderService;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignFacadeImpl implements CampaignFacade {

  private final PreorderCampaignService preorderCampaignService;
  private final BlindboxSeriesService blindboxSeriesService;
  private final CampaignTierService campaignTierService;
  private final PreorderService preorderService;
  private final ModelMapper mapper;

  @Override
  public BaseResponse<PreorderCampaignDetailsManagementResponse> getCampaignDetails(Long id) {
    PreorderCampaignDetailsManagementResponse campaign = mapper.map(preorderCampaignService.getCampaignById(id), PreorderCampaignDetailsManagementResponse.class);
    List<CampaignTier> campaignTiers = campaignTierService.getCampaignTiersByCampaignId(id);
    List<CampaignTierResponse> campaignTierResponses = campaignTiers.stream()
            .map(campaignTier -> mapper.map(campaignTier, CampaignTierResponse.class))
            .collect(Collectors.toList());
    campaign.setCampaignTiers(campaignTierResponses);
    campaign.setCurrentUnitsCount(preorderCampaignService.getCurrentUnitsCountOfCampaign(id));
    campaign.setTotalDiscountedUnits(preorderCampaignService.getTotalDiscountedUnitsOfCampaign(id));
    return BaseResponse.build(campaign, true);
  }

  @Override
  public BaseResponse<List<PreorderCampaignManagementResponse>> getAllCampaignsOfSeries(Long seriesId) {
    List<PreorderCampaign> campaigns = preorderCampaignService.getCampaignsByBlindboxSeriesId(seriesId);
    List<PreorderCampaignManagementResponse> response = campaigns.stream()
            .map(campaign -> {
                PreorderCampaignManagementResponse preorderCampaignManagementResponse = mapper.map(campaign, PreorderCampaignManagementResponse.class);
                preorderCampaignManagementResponse.setCurrentUnitsCount(preorderCampaignService.getCurrentUnitsCountOfCampaign(campaign.getId()));
                preorderCampaignManagementResponse.setTotalDiscountedUnits(preorderCampaignService.getTotalDiscountedUnitsOfCampaign(campaign.getId()));
                return preorderCampaignManagementResponse;
            })
            .collect(Collectors.toList());
    return BaseResponse.build(response, true);
  }

  @Override
  @Transactional
  public BaseResponse<PreorderCampaignDetailsManagementResponse> createCampaign(PreorderCampaignRequest request) {
    PreorderCampaign ongoingCampaign = preorderCampaignService.getOngoingCampaignOfBlindboxSeries(request.getBlindboxSeriesId()).orElse(null);
    if (ongoingCampaign != null) {
      throw new IllegalArgumentException("There is an ongoing campaign for this blindbox series, campaign ID is " + ongoingCampaign.getId());
    }

    PreorderCampaign preorderCampaign = mapper.map(request, PreorderCampaign.class);
    preorderCampaign.setBlindboxSeries(blindboxSeriesService
            .getBlindboxSeriesById(request.getBlindboxSeriesId())
            .orElseThrow(() -> new IllegalArgumentException("Blindbox series not found")));

      preorderCampaign.setCampaignTiers(new java.util.ArrayList<>());

    List<CampaignTier> campaignTiers = IntStream.range(0, request.getCampaignTiers().size())
            .mapToObj(i -> {
              CampaignTier campaignTier = mapper.map(request.getCampaignTiers().get(i), CampaignTier.class);
              campaignTier.setCurrentCount(0);
              campaignTier.setTierStatus(i == 0 ? TierStatus.PROCESSING : TierStatus.UPCOMING);
              campaignTier.setCampaign(preorderCampaign);
              return campaignTier;
            })
            .toList();

    preorderCampaign.getCampaignTiers().addAll(campaignTiers);
    preorderCampaignService.validateCampaignTiers(preorderCampaign);

    preorderCampaignService.createCampaign(preorderCampaign);

    campaignTiers.forEach(campaignTierService::createCampaignTier);

    PreorderCampaignDetailsManagementResponse response = mapper.map(preorderCampaign, PreorderCampaignDetailsManagementResponse.class);
    response.setSeries(mapper.map(preorderCampaign.getBlindboxSeries(), com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesResponse.class));
    response.setCampaignTiers(campaignTiers.stream()
            .map(campaignTier -> mapper.map(campaignTier, CampaignTierResponse.class))
            .collect(Collectors.toList()));

    return BaseResponse.build(response, true);
  }

  @Override
  public BaseResponse<Void> endCampaign(Long campaignId) {
    PreorderCampaign campaign = preorderCampaignService.getCampaignById(campaignId);
    if (campaign == null) {
      throw new IllegalArgumentException("Campaign not found");
    }

    if (campaign.getCampaignType() == CampaignType.GROUP) {
      BigDecimal finalDiscount = BigDecimal.valueOf(preorderCampaignService.getDiscountOfActiveTierOfOnGoingCampaign(campaignId));
      Set<Long> preorderIds = preorderService
              .getPreorderItemsAssociatedWithBlindboxSeries(campaign.getBlindboxSeries().getId())
              .stream()
              .filter(item -> item.getItemFromCampaignType() == CampaignType.GROUP)
              .peek(item -> {
                BigDecimal originalPrice = item.getOriginalPrice();
                if (originalPrice == null) {
                  throw new IllegalStateException("Original price not set for preorder item");
                }
                item.setLockedPrice(Util.calculatePriceWithCoefficient(originalPrice, finalDiscount));
                preorderService.updatePreorderItem(item);
              })
              .map(item -> item.getPreorder().getId())
              .collect(Collectors.toSet());

        preorderIds.forEach(preorderService::updatePreorderPrice);
    }
    preorderCampaignService.endCampaign(campaignId);
    return BaseResponse.ok();
  }
}
