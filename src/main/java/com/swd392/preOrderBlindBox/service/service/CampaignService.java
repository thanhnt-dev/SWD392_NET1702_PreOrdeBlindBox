package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.PreorderCampaign;

import java.util.List;

public interface CampaignService {
    List<PreorderCampaign> getAllCampaigns();

    List<PreorderCampaign> getAllActiveCampaigns();

    PreorderCampaign getCampaignById(Long id);

    List<PreorderCampaign> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

    List<PreorderCampaign> getActiveCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

    PreorderCampaign createCampaign(PreorderCampaign preorderCampaign);

    PreorderCampaign updateCampaign(PreorderCampaign preorderCampaign, Long id);

    void deleteCampaign(Long id);
}
