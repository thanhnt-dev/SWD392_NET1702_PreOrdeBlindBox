package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.facade.facade.CampaignFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.PreorderCampaignRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/campaigns")
@RequiredArgsConstructor
public class CampaignController {

  private final CampaignFacade campaignFacade;

  @PostMapping()
  @PreAuthorize("hasRole('ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Create campaign for Admin",
      tags = {"Campaign APIs"})
  BaseResponse<Void> createCampaign(@RequestBody PreorderCampaignRequest request) {
    return campaignFacade.createCampaignTier(request);
  }
}
