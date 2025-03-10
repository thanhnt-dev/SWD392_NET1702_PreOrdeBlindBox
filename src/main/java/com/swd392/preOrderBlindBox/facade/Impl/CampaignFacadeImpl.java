package com.swd392.preOrderBlindBox.facade.Impl;

import com.swd392.preOrderBlindBox.facade.CampaignFacade;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.CampaignTierResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignFacadeImpl implements CampaignFacade {
    @Override
    public BaseResponse<List<CampaignTierResponse>> getAllCampaignTiers(Long campaignId) {
        return null;
    }

    @Override
    public BaseResponse<CampaignTierResponse> getCampaignTierWithDetailsById(Long id) {
        return null;
    }


}
