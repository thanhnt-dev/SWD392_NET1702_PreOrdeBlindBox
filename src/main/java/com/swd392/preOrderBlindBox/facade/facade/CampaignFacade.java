package com.swd392.preOrderBlindBox.facade.facade;

import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.CampaignTierResponse;

import java.util.List;

public interface CampaignFacade {
    BaseResponse<List<CampaignTierResponse>> getAllCampaignTiers(Long campaignId);

    BaseResponse<CampaignTierResponse> getCampaignTierWithDetailsById(Long id);
}
