package com.swd392.preOrderBlindBox.service;

import com.swd392.preOrderBlindBox.entity.Campaign;

import java.math.BigDecimal;
import java.util.List;

public interface CampaignService {
    List<Campaign> getAllCampaigns();

    List<Campaign> getAllActiveCampaigns();

    Campaign getCampaignById(Long id);

    List<Campaign> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

    List<Campaign> getActiveCampaignsByBlindboxSeriesId(Long blindboxSeriesId);

    Campaign createCampaign(Campaign campaign);

    Campaign updateCampaign(Campaign campaign, Long id);

    /**
     * Updates the locked price for a campaign
     *
     * @param campaignId the ID of the campaign to update
     * @param lockedPrice the price to lock
     * @return the updated campaign
     */
    Campaign updateLockedPrice(Long campaignId, BigDecimal lockedPrice);

    void deleteCampaign(Long id);
}