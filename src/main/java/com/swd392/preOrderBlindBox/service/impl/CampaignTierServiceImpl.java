package com.swd392.preOrderBlindBox.service.impl;

import com.swd392.preOrderBlindBox.entity.CampaignTier;
import com.swd392.preOrderBlindBox.enums.ErrorCode;
import com.swd392.preOrderBlindBox.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.CampaignTierRepository;
import com.swd392.preOrderBlindBox.service.CampaignTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignTierServiceImpl implements CampaignTierService {
    private final CampaignTierRepository campaignTierRepository;

    @Override
    public List<CampaignTier> getAllCampaignTiers() {
        return campaignTierRepository.findAll();
    }

    @Override
    public CampaignTier getCampaignTierById(Long id) {
        return campaignTierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public List<CampaignTier> getCampaignTiersByCampaignId(Long campaignId) {
        return campaignTierRepository.findByCampaignId(campaignId);
    }

    @Override
    public CampaignTier createCampaignTier(CampaignTier campaignTier) {
        return campaignTierRepository.save(campaignTier);
    }

    @Override
    public CampaignTier updateCampaignTier(CampaignTier campaignTier, Long id) {
        CampaignTier oldCampaignTier = campaignTierRepository.findById(id).orElse(null);
        if (oldCampaignTier == null) {
            return null;
        }
        oldCampaignTier.setDiscountPercent(campaignTier.getDiscountPercent());
        oldCampaignTier.setTierName(campaignTier.getTierName());
        oldCampaignTier.setDiscountPercent(campaignTier.getDiscountPercent());
        oldCampaignTier.setMaxQuantity(campaignTier.getMaxQuantity());
        oldCampaignTier.setMinQuantity(campaignTier.getMinQuantity());

        return campaignTierRepository.save(oldCampaignTier);
    }

    @Override
    public void deleteCampaignTier(Long id) {
        campaignTierRepository.deleteById(id);
    }
}
