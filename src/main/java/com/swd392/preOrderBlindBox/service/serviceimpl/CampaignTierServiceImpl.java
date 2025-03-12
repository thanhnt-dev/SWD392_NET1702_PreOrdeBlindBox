package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.entity.CampaignTier;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.repository.CampaignTierRepository;
import com.swd392.preOrderBlindBox.service.service.CampaignTierService;
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
        oldCampaignTier.setAlias(campaignTier.getAlias());
        oldCampaignTier.setCurrentCount(campaignTier.getCurrentCount());
        oldCampaignTier.setThresholdQuantity(campaignTier.getThresholdQuantity());
        oldCampaignTier.setTierOrder(oldCampaignTier.getTierOrder());
        oldCampaignTier.setDiscountPercent(campaignTier.getDiscountPercent());
        oldCampaignTier.setTierStatus(campaignTier.getTierStatus());

        return campaignTierRepository.save(oldCampaignTier);
    }

    @Override
    public void deleteCampaignTier(Long id) {
        campaignTierRepository.deleteById(id);
    }
}
