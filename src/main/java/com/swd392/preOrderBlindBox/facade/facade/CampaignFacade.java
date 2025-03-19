package com.swd392.preOrderBlindBox.facade.facade;

import com.swd392.preOrderBlindBox.restcontroller.request.PreorderCampaignRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderCampaignDetailsManagementResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderCampaignManagementResponse;

import java.util.List;

public interface CampaignFacade {
  BaseResponse<PreorderCampaignDetailsManagementResponse> getCampaignDetails(Long id);

  BaseResponse<List<PreorderCampaignManagementResponse>> getAllCampaignsOfSeries(Long seriesId);

  BaseResponse<PreorderCampaignDetailsManagementResponse> createCampaign(PreorderCampaignRequest request);

  BaseResponse<Void> endCampaign(Long campaignId);
}
