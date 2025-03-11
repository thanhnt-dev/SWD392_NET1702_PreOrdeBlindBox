package com.swd392.preOrderBlindBox.service.impl;

import com.swd392.preOrderBlindBox.entity.Campaign;
import com.swd392.preOrderBlindBox.enums.ErrorCode;
import com.swd392.preOrderBlindBox.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.CampaignRepository;
import com.swd392.preOrderBlindBox.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;

    @Override
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    @Override
    public List<Campaign> getAllActiveCampaigns() {
        // Updated method name to match repository
        return campaignRepository.findAllByIsActiveTrue();
    }

    @Override
    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public List<Campaign> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId) {
        return campaignRepository.findByBlindboxSeriesId(blindboxSeriesId);
    }

    @Override
    public List<Campaign> getActiveCampaignsByBlindboxSeriesId(Long blindboxSeriesId) {
        // Updated method name to match repository
        return campaignRepository.findByBlindboxSeriesIdAndIsActiveTrue(blindboxSeriesId);
    }

    @Override
    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign updateCampaign(Campaign campaign, Long id) {
        if (!campaign.getId().equals(id)) {
            throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        Campaign existingCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        existingCampaign.setBasePrice(campaign.getBasePrice());
        existingCampaign.setCampaignType(campaign.getCampaignType());
        existingCampaign.setCurrentPlacedBlindbox(campaign.getCurrentPlacedBlindbox());
        existingCampaign.setDepositPercent(campaign.getDepositPercent());
        existingCampaign.setEndCampaignTime(campaign.getEndCampaignTime());
        existingCampaign.setStartCampaignTime(campaign.getStartCampaignTime());
        existingCampaign.setTargetBlindboxQuantity(campaign.getTargetBlindboxQuantity());
        // Don't update locked price here to preserve historical accuracy

        return campaignRepository.save(existingCampaign);
    }

    @Override
    public Campaign updateLockedPrice(Long campaignId, BigDecimal lockedPrice) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.setLockedPrice(lockedPrice);
        return campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }
}