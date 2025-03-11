package com.swd392.preOrderBlindBox.facade;

import com.swd392.preOrderBlindBox.request.CampaignCreateRequest;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.CampaignDetailsResponse;
import com.swd392.preOrderBlindBox.response.CampaignResponse;
import com.swd392.preOrderBlindBox.response.CampaignTierResponse;

import java.util.List;

public interface CampaignFacade {
    // Existing methods
    BaseResponse<List<CampaignTierResponse>> getAllCampaignTiers(Long campaignId);
    BaseResponse<CampaignTierResponse> getCampaignTierWithDetailsById(Long id);

    // New methods
    BaseResponse<CampaignDetailsResponse> createCampaign(CampaignCreateRequest request);
    BaseResponse<List<CampaignResponse>> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId);
    BaseResponse<CampaignDetailsResponse> getCampaignById(Long id);
}