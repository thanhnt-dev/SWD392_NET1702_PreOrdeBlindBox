package com.swd392.preOrderBlindBox.service;

import com.swd392.preOrderBlindBox.entity.Campaign;

import java.util.List;

public interface CampaignService {
    List<Campaign> getAllCampaigns();

    List<Campaign> getAllActiveCampaigns();

    Campaign getCampaignById(Long id);

    List<Campaign> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

    List<Campaign> getActiveCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

    Campaign createCampaign(Campaign campaign);

    Campaign updateCampaign(Campaign campaign, Long id);

    void deleteCampaign(Long id);
}
