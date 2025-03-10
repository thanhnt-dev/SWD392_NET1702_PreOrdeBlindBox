package com.swd392.preOrderBlindBox.facade;

import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.CampaignTierResponse;

import java.util.List;

public interface CampaignFacade {
    BaseResponse<List<CampaignTierResponse>> getAllCampaignTiers(Long campaignId);

    BaseResponse<CampaignTierResponse> getCampaignTierWithDetailsById(Long id);
}
