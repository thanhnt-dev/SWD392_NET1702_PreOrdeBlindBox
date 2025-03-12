package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.repository.repository.CampaignTierRepository;
import com.swd392.preOrderBlindBox.repository.repository.PreorderCampaignRepository;
import com.swd392.preOrderBlindBox.service.service.PreorderCampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreorderCampaignServiceImpl implements PreorderCampaignService {
    private final PreorderCampaignRepository preorderCampaignRepository;
    private final CampaignTierRepository campaignTierRepository;

    @Override
    public List<PreorderCampaign> getAllCampaigns() {
        return preorderCampaignRepository.findAll();
    }

    @Override
    public Optional<PreorderCampaign> getOngoingCampaignOfBlindboxSeries(Long seriesId) {
        List<PreorderCampaign> campaigns = preorderCampaignRepository.findByBlindboxSeriesId(seriesId);
        PreorderCampaign result = campaigns.stream()
                .filter(campaign ->
                        campaign.getStartCampaignTime().isBefore(LocalDateTime.now()) &&
                                campaign.getEndCampaignTime().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
        return Optional.ofNullable(result);
    }

    @Override
    public PreorderCampaign getCampaignById(Long id) {
        return preorderCampaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    }

    @Override
    public List<PreorderCampaign> getCampaignsByBlindboxSeriesId(Long blindboxSeriesId) {
        return preorderCampaignRepository.findByBlindboxSeriesId(blindboxSeriesId);
    }

    @Override
    public List<PreorderCampaign> getActiveCampaignsByBlindboxSeriesId(Long blindboxSeriesId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreorderCampaign createCampaign(PreorderCampaign preorderCampaign) {
        return preorderCampaignRepository.save(preorderCampaign);
    }

    @Override
    public PreorderCampaign updateCampaign(PreorderCampaign preorderCampaign, Long id) {
        if (!preorderCampaign.getId().equals(id)) {
            throw new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        PreorderCampaign existingPreorderCampaign = preorderCampaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        existingPreorderCampaign.setCampaignType(preorderCampaign.getCampaignType());
        existingPreorderCampaign.setEndCampaignTime(preorderCampaign.getEndCampaignTime());
        existingPreorderCampaign.setStartCampaignTime(preorderCampaign.getStartCampaignTime());

        return preorderCampaignRepository.save(existingPreorderCampaign);
    }

    @Override
    public void deleteCampaign(Long id) {
        preorderCampaignRepository.deleteById(id);
    }
}
