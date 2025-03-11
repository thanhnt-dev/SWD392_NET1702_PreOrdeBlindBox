package com.swd392.preOrderBlindBox.facade.Impl;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.entity.Campaign;
import com.swd392.preOrderBlindBox.entity.CampaignTier;
import com.swd392.preOrderBlindBox.enums.CampaignType;
import com.swd392.preOrderBlindBox.enums.ErrorCode;
import com.swd392.preOrderBlindBox.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.facade.CampaignFacade;
import com.swd392.preOrderBlindBox.request.CampaignCreateRequest;
import com.swd392.preOrderBlindBox.request.CampaignTierCreateRequest;
import com.swd392.preOrderBlindBox.response.*;
import com.swd392.preOrderBlindBox.service.BlindboxSeriesService;
import com.swd392.preOrderBlindBox.service.CampaignService;
import com.swd392.preOrderBlindBox.service.CampaignTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignFacadeImpl implements CampaignFacade {
    private final CampaignService campaignService;
    private final CampaignTierService campaignTierService;
    private final BlindboxSeriesService blindboxSeriesService;

    @Override
    public BaseResponse<List<CampaignTierResponse>> getAllCampaignTiers(Long campaignId) {
        List<CampaignTier> campaignTiers = campaignTierService.getCampaignTiersByCampaignId(campaignId);
        List<CampaignTierResponse> responseList = campaignTiers.stream()
                .map(this::toCampaignTierResponse)
                .collect(Collectors.toList());
        return BaseResponse.build(responseList, true);
    }

    @Override
    public BaseResponse<CampaignTierResponse> getCampaignTierWithDetailsById(Long id) {
        CampaignTier campaignTier = campaignTierService.getCampaignTierById(id);
        CampaignTierResponse response = toCampaignTierResponse(campaignTier);
        return BaseResponse.build(response, true);
    }

    @Override
    @Transactional
    public BaseResponse<CampaignDetailsResponse> createCampaign(CampaignCreateRequest request) {
        // Validate blindbox series exists
        BlindboxSeries blindboxSeries = blindboxSeriesService.getBlindboxSeriesById(request.getBlindboxSeriesId());

        // Validate campaign data
        validateCampaignRequest(request);

        // Create campaign
        Campaign campaign = Campaign.builder()
                .blindboxSeries(blindboxSeries)
                .campaignType(request.getCampaignType())
                .startCampaignTime(request.getStartCampaignTime())
                .endCampaignTime(request.getEndCampaignTime())
                .currentPlacedBlindbox(0)
                .targetBlindboxQuantity(request.getTargetBlindboxQuantity())
                .depositPercent(request.getDepositPercent())
                .basePrice(request.getBasePrice())
                .build();

        Campaign savedCampaign = campaignService.createCampaign(campaign);

        // Create campaign tiers
        List<CampaignTier> savedTiers = new ArrayList<>();
        if (request.getTiers() != null && !request.getTiers().isEmpty()) {
            for (CampaignTierCreateRequest tierRequest : request.getTiers()) {
                CampaignTier tier = CampaignTier.builder()
                        .campaign(savedCampaign)
                        .tierName(tierRequest.getTierName())
                        .minQuantity(tierRequest.getMinQuantity())
                        .maxQuantity(tierRequest.getMaxQuantity())
                        .discountPercent(tierRequest.getDiscountPercent())
                        .build();
                savedTiers.add(campaignTierService.createCampaignTier(tier));
            }
        }

        CampaignDetailsResponse response = toCampaignDetailsResponse(savedCampaign, savedTiers);
        return BaseResponse.build(response, true);
    }

    @Override
    public BaseResponse<List<CampaignResponse>> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId) {
        // Validate blindbox series exists
        blindboxSeriesService.getBlindboxSeriesById(blindboxSeriesId);

        // Get active campaigns for the blindbox series
        List<Campaign> campaigns = campaignService.getActiveCampaignsByBlindboxSeriesId(blindboxSeriesId);

        // For each campaign, ensure locked price is set if needed
        for (Campaign campaign : campaigns) {
            if (campaign.getCampaignType() == CampaignType.GROUP && campaign.getLockedPrice() == null) {
                List<CampaignTier> tiers = campaignTierService.getCampaignTiersByCampaignId(campaign.getId());
                CampaignTier activeTier = findActiveTier(tiers, campaign.getCurrentPlacedBlindbox());

                if (activeTier != null) {
                    BigDecimal calculatedPrice = campaign.calculateEffectivePrice(activeTier.getDiscountPercent());
                    campaignService.updateLockedPrice(campaign.getId(), calculatedPrice);
                }
            }
        }

        // Refresh campaigns to get updated data
        campaigns = campaignService.getActiveCampaignsByBlindboxSeriesId(blindboxSeriesId);

        List<CampaignResponse> responseList = campaigns.stream()
                .map(this::toCampaignResponse)
                .collect(Collectors.toList());

        return BaseResponse.build(responseList, true);
    }

    @Override
    public BaseResponse<CampaignDetailsResponse> getCampaignById(Long id) {
        Campaign campaign = campaignService.getCampaignById(id);
        List<CampaignTier> campaignTiers = campaignTierService.getCampaignTiersByCampaignId(id);

        // Update the locked price if necessary for GROUP type campaigns
        if (campaign.getCampaignType() == CampaignType.GROUP && campaign.getLockedPrice() == null) {
            CampaignTier activeTier = findActiveTier(campaignTiers, campaign.getCurrentPlacedBlindbox());
            if (activeTier != null) {
                BigDecimal calculatedPrice = campaign.calculateEffectivePrice(activeTier.getDiscountPercent());
                campaign = campaignService.updateLockedPrice(id, calculatedPrice);
            }
        }

        CampaignDetailsResponse response = toCampaignDetailsResponse(campaign, campaignTiers);
        return BaseResponse.build(response, true);
    }

    /**
     * Finds the active tier based on current placed quantity
     *
     * @param tiers List of campaign tiers
     * @param currentPlaced Current placed quantity
     * @return The active tier or null if no tier is active
     */
    private CampaignTier findActiveTier(List<CampaignTier> tiers, int currentPlaced) {
        return tiers.stream()
                .filter(tier -> isTierActive(tier, currentPlaced))
                .findFirst()
                .orElse(null);
    }

    private CampaignResponse toCampaignResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .campaignType(campaign.getCampaignType())
                .startCampaignTime(campaign.getStartCampaignTime())
                .endCampaignTime(campaign.getEndCampaignTime())
                .currentPlacedBlindbox(campaign.getCurrentPlacedBlindbox())
                .targetBlindboxQuantity(campaign.getTargetBlindboxQuantity())
                .depositPercent(campaign.getDepositPercent())
                .basePrice(campaign.getBasePrice())
                .lockedPrice(campaign.getLockedPrice())
                .blindboxSeriesId(campaign.getBlindboxSeries().getId())
                .blindboxSeriesName(campaign.getBlindboxSeries().getSeriesName())
                .build();
    }

    private CampaignDetailsResponse toCampaignDetailsResponse(Campaign campaign, List<CampaignTier> tiers) {
        BlindboxSeries blindboxSeries = campaign.getBlindboxSeries();
        BlindboxSeriesResponse blindboxSeriesResponse = BlindboxSeriesResponse.builder()
                .id(blindboxSeries.getId())
                .seriesName(blindboxSeries.getSeriesName())
                .description(blindboxSeries.getDescription())
                .openedAt(blindboxSeries.getOpenedAt())
                .category(blindboxSeries.getCategory() != null ?
                        CategoryResponse.builder()
                                .id(blindboxSeries.getCategory().getId())
                                .categoryName(blindboxSeries.getCategory().getCategoryName())
                                .build() : null)
                .build();

        return CampaignDetailsResponse.builder()
                .id(campaign.getId())
                .campaignType(campaign.getCampaignType())
                .startCampaignTime(campaign.getStartCampaignTime())
                .endCampaignTime(campaign.getEndCampaignTime())
                .currentPlacedBlindbox(campaign.getCurrentPlacedBlindbox())
                .targetBlindboxQuantity(campaign.getTargetBlindboxQuantity())
                .depositPercent(campaign.getDepositPercent())
                .basePrice(campaign.getBasePrice())
                .lockedPrice(campaign.getLockedPrice())
                .blindboxSeriesId(blindboxSeries.getId())
                .blindboxSeriesName(blindboxSeries.getSeriesName())
                .blindboxSeries(blindboxSeriesResponse)
                .campaignTiers(tiers.stream()
                        .map(this::toCampaignTierResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private CampaignTierResponse toCampaignTierResponse(CampaignTier tier) {
        Campaign campaign = tier.getCampaign();
        boolean isActive = false;

        // Determine if tier is active based on campaign's current placed blindbox count
        if (campaign != null) {
            int currentPlaced = campaign.getCurrentPlacedBlindbox();
            isActive = isTierActive(tier, currentPlaced);
        }

        return CampaignTierResponse.builder()
                .id(tier.getId())
                .tierName(tier.getTierName())
                .minQuantity(tier.getMinQuantity())
                .maxQuantity(tier.getMaxQuantity())
                .discountPercent(tier.getDiscountPercent())
                .isActive(isActive)
                .build();
    }

    /**
     * Method to determine if a tier is active based on current placed quantity
     *
     * @param tier The campaign tier to check
     * @param currentPlaced The current placed quantity
     * @return boolean indicating if the tier is active
     */
    private boolean isTierActive(CampaignTier tier, int currentPlaced) {
        if (currentPlaced < tier.getMinQuantity()) {
            return false;
        }

        // For GROUP type campaigns or tiers with no max quantity,
        // the tier is active if currentPlaced >= minQuantity
        if (tier.getMaxQuantity() == null) {
            return true;
        }

        // For MILESTONE type campaigns, check if currentPlaced is within range
        return currentPlaced <= tier.getMaxQuantity();
    }

    private void validateCampaignRequest(CampaignCreateRequest request) {
        // Validate start and end time
        if (request.getStartCampaignTime().isAfter(request.getEndCampaignTime())) {
            throw new IllegalArgumentException("Start campaign time must be before end campaign time");
        }

        // Validate campaign type specific requirements
        if (request.getCampaignType() == CampaignType.GROUP) {
            // For GROUP type, deposit percent is required
            if (request.getDepositPercent() == null || request.getDepositPercent() <= 0 || request.getDepositPercent() >= 100) {
                throw new IllegalArgumentException("Deposit percent is required for GROUP type campaigns and must be between 1 and 99");
            }
        }

        // Validate tiers
        if (request.getTiers() == null || request.getTiers().isEmpty()) {
            throw new IllegalArgumentException("At least one tier is required");
        }

        // Validate tiers based on campaign type
        validateTiers(request.getTiers(), request.getCampaignType(), request.getTargetBlindboxQuantity());
    }

    private void validateTiers(List<CampaignTierCreateRequest> tiers, CampaignType campaignType, int targetQuantity) {
        Integer previousMax = null;

        for (int i = 0; i < tiers.size(); i++) {
            CampaignTierCreateRequest tier = tiers.get(i);

            // Common validations
            if (tier.getMinQuantity() < 0) {
                throw new IllegalArgumentException("Minimum quantity must be non-negative");
            }

            if (tier.getDiscountPercent() < 0 || tier.getDiscountPercent() > 100) {
                throw new IllegalArgumentException("Discount percent must be between 0 and 100");
            }

            // Campaign type specific validations
            if (campaignType == CampaignType.GROUP) {
                // For GROUP type, max quantity should be null
                if (tier.getMaxQuantity() != null) {
                    throw new IllegalArgumentException("Max quantity must be null for GROUP type campaigns");
                }
            } else if (campaignType == CampaignType.MILESTONE) {
                // For MILESTONE type, max quantity is required
                if (tier.getMaxQuantity() == null) {
                    throw new IllegalArgumentException("Max quantity is required for MILESTONE type campaigns");
                }

                if (tier.getMaxQuantity() <= tier.getMinQuantity()) {
                    throw new IllegalArgumentException("Max quantity must be greater than min quantity");
                }

                // Ensure tiers are sequential
                if (previousMax != null && tier.getMinQuantity() != previousMax + 1) {
                    throw new IllegalArgumentException(
                            "Min quantity of tier " + (i + 1) + " must be exactly one more than max quantity of tier " + i);
                }

                previousMax = tier.getMaxQuantity();

                // For the last tier, max quantity should match target quantity
                if (i == tiers.size() - 1 && !tier.getMaxQuantity().equals(targetQuantity)) {
                    throw new IllegalArgumentException("Max quantity of the last tier must equal the target quantity");
                }
            }
        }
    }
}