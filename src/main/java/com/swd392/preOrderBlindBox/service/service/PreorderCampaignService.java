package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import java.util.List;
import java.util.Optional;

public interface PreorderCampaignService {
  List<PreorderCampaign> getAllCampaigns();

  Optional<PreorderCampaign> getOngoingCampaignOfBlindboxSeries(Long seriesId);

  PreorderCampaign getCampaignById(Long id);

  List<PreorderCampaign> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

  List<PreorderCampaign> getActiveCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

  PreorderCampaign createCampaign(PreorderCampaign preorderCampaign);

  PreorderCampaign updateCampaign(PreorderCampaign preorderCampaign, Long id);

  void deleteCampaign(Long id);

  int getCurrentUnitsCountOfActiveTierOfOngoingCampaign(Long campaignId);

  int getDiscountOfActiveTierOfOnGoingCampaign(Long campaignId);
}
