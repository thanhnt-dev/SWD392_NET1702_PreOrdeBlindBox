package com.swd392.preOrderBlindBox.controller;

import com.swd392.preOrderBlindBox.facade.CampaignFacade;
import com.swd392.preOrderBlindBox.request.CampaignCreateRequest;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.CampaignDetailsResponse;
import com.swd392.preOrderBlindBox.response.CampaignResponse;
import com.swd392.preOrderBlindBox.response.CampaignTierResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;     //test@example.com, password: Test123@

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaign APIs", description = "APIs for managing campaigns")
public class CampaignController {
    private final CampaignFacade campaignFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new campaign",
            description = "Creates a new campaign with tiers for a blindbox series",
            tags = {"Campaign APIs"},
            security = { @SecurityRequirement(name = "Bearer Authentication") }
    )
    public BaseResponse<CampaignDetailsResponse> createCampaign(@Validated @RequestBody CampaignCreateRequest request) {
        return campaignFacade.createCampaign(request);
    }

    @GetMapping("/blindbox-series/{blindboxSeriesId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get campaigns by blindbox series ID",
            description = "Returns all active campaigns for a blindbox series",
            tags = {"Campaign APIs"}
    )
    public BaseResponse<List<CampaignResponse>> getCampaignsByBlindboxSeriesId(@PathVariable Long blindboxSeriesId) {
        return campaignFacade.getCampaignsByBlindboxSeriesId(blindboxSeriesId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get campaign details by ID",
            description = "Returns detailed information about a campaign including its tiers",
            tags = {"Campaign APIs"}
    )
    public BaseResponse<CampaignDetailsResponse> getCampaignById(@PathVariable Long id) {
        return campaignFacade.getCampaignById(id);
    }

    @GetMapping("/{id}/tiers")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get campaign tiers by campaign ID",
            description = "Returns all tiers for a specific campaign",
            tags = {"Campaign APIs"}
    )
    public BaseResponse<List<CampaignTierResponse>> getCampaignTiers(@PathVariable Long id) {
        return campaignFacade.getAllCampaignTiers(id);
    }
}