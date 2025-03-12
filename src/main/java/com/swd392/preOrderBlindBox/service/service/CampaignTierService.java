package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.CampaignTier;

import java.util.List;

public interface CampaignTierService {
    List<CampaignTier> getAllCampaignTiers();

    CampaignTier getCampaignTierById(Long id);

    CampaignTier createCampaignTier(CampaignTier campaignTier);

    CampaignTier updateCampaignTier(CampaignTier campaignTier, Long id);

    void deleteCampaignTier(Long id);

    List<CampaignTier> getCampaignTiersByCampaignId(Long campaignId);
}
