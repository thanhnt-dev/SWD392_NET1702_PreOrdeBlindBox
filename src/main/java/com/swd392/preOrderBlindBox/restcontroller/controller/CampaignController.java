package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.facade.facade.CampaignFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderCampaignRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderCampaignDetailsManagementResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderCampaignManagementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${api.version}/campaigns")
@RequiredArgsConstructor
public class CampaignController {

  private final CampaignFacade campaignFacade;

  @PostMapping()
  @PreAuthorize("hasRole('STAFF')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Create preorder campaign",
      tags = {"Campaign APIs"})
  BaseResponse<PreorderCampaignDetailsManagementResponse> createCampaign(@RequestBody PreorderCampaignRequest request) {
    return campaignFacade.createCampaign(request);
  }

  @GetMapping("/series/{seriesId}")
  @PreAuthorize("hasRole('STAFF')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
          summary = "Get all campaigns of a blindbox series",
          tags = {"Campaign APIs"})
  BaseResponse<List<PreorderCampaignManagementResponse>> getCampaignsOfBlindboxSeries(@Valid @PathVariable Long seriesId) {
    return campaignFacade.getAllCampaignsOfSeries(seriesId);
  }

  @GetMapping("/{campaignId}")
  @PreAuthorize("hasRole('STAFF')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
          summary = "Get details of a campaign",
          tags = {"Campaign APIs"})
  BaseResponse<PreorderCampaignDetailsManagementResponse> getCampaignDetails(@Valid @PathVariable Long campaignId) {
    return campaignFacade.getCampaignDetails(campaignId);
  }

  @PutMapping("/{campaignId}/end")
  @PreAuthorize("hasRole('STAFF')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
          summary = "End a campaign and update associated preordered items",
          tags = {"Campaign APIs"})
  BaseResponse<Void> endCampaign(@Valid @PathVariable Long campaignId) {
    return campaignFacade.endCampaign(campaignId);
  }
}
