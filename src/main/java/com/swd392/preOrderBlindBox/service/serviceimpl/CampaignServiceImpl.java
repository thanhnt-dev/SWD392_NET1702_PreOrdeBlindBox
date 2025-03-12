package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.repository.CampaignRepository;
import com.swd392.preOrderBlindBox.service.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;

    @Override
    public List<PreorderCampaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    @Override
    public List<PreorderCampaign> getAllActiveCampaigns() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreorderCampaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public List<PreorderCampaign> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId) {
        return campaignRepository.findByBlindboxSeriesId(blindboxSeriesId);
    }

    @Override
    public List<PreorderCampaign> getActiveCampaignsByBlindboxSeriesId(Long blindboxSeriesId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreorderCampaign createCampaign(PreorderCampaign preorderCampaign) {
        return campaignRepository.save(preorderCampaign);
    }

    @Override
    public PreorderCampaign updateCampaign(PreorderCampaign preorderCampaign, Long id) {
        if (!preorderCampaign.getId().equals(id)) {
            throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        PreorderCampaign existingPreorderCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        existingPreorderCampaign.setCampaignType(preorderCampaign.getCampaignType());
        existingPreorderCampaign.setEndCampaignTime(preorderCampaign.getEndCampaignTime());
        existingPreorderCampaign.setStartCampaignTime(preorderCampaign.getStartCampaignTime());

        return campaignRepository.save(existingPreorderCampaign);
    }

    @Override
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }
}
