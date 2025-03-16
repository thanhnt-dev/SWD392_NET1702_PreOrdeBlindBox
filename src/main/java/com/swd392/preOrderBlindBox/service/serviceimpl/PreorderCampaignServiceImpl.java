package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.enums.TierStatus;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.CampaignTier;
import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import com.swd392.preOrderBlindBox.repository.repository.CampaignTierRepository;
import com.swd392.preOrderBlindBox.repository.repository.PreorderCampaignRepository;
import com.swd392.preOrderBlindBox.service.service.PreorderCampaignService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    LocalDateTime now = LocalDateTime.now();

    return campaigns.stream()
        .filter(
            campaign ->
                campaign.getStartCampaignTime().isBefore(now)
                    && campaign.getEndCampaignTime().isAfter(now))
        .findFirst();
  }

  @Override
  public PreorderCampaign getCampaignById(Long id) {
    return preorderCampaignRepository
        .findById(id)
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

    PreorderCampaign existingPreorderCampaign =
        preorderCampaignRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    existingPreorderCampaign.setCampaignType(preorderCampaign.getCampaignType());
    existingPreorderCampaign.setEndCampaignTime(preorderCampaign.getEndCampaignTime());
    existingPreorderCampaign.setStartCampaignTime(preorderCampaign.getStartCampaignTime());

    return preorderCampaignRepository.save(existingPreorderCampaign);
  }

  @Override
  public int getCurrentUnitsCountOfActiveTierOfOngoingCampaign(Long campaignId) {
    validateOngoingCampaign(campaignId);
    return findActiveTier(campaignId).map(CampaignTier::getCurrentCount).orElse(0);
  }

  @Override
  public int getDiscountOfActiveTierOfOnGoingCampaign(Long campaignId) {
    validateOngoingCampaign(campaignId);
    return findActiveTier(campaignId).map(CampaignTier::getDiscountPercent).orElse(0);
  }

  @Override
  public void incrementUnitsCount(Long campaignId, int unitsCount) {
    CampaignTier activeTier = findActiveTier(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    activeTier.setCurrentCount(activeTier.getCurrentCount() + unitsCount);
  }

  private void validateOngoingCampaign(Long campaignId) {
    PreorderCampaign campaign =
        preorderCampaignRepository
            .findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    // Ensure the campaign is active and ongoing
    if (!campaign.isActive()
        || campaign.getStartCampaignTime().isAfter(LocalDateTime.now())
        || campaign.getEndCampaignTime().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("No active ongoing campaign found for the given ID.");
    }
  }

  private Optional<CampaignTier> findActiveTier(Long campaignId) {
    List<CampaignTier> tiers = campaignTierRepository.findByCampaignId(campaignId);
    return tiers.stream().filter(tier -> tier.getTierStatus() == TierStatus.PROCESSING).findFirst();
  }

  @Override
  public void deleteCampaign(Long id) {
    preorderCampaignRepository.deleteById(id);
  }
}
