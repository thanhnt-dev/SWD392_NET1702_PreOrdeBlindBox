package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.enums.TierStatus;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.CampaignTier;
import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import com.swd392.preOrderBlindBox.repository.repository.CampaignTierRepository;
import com.swd392.preOrderBlindBox.repository.repository.PreorderCampaignRepository;
import com.swd392.preOrderBlindBox.service.service.PreorderCampaignService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
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
    return getActiveTier(campaignId).map(CampaignTier::getCurrentCount).orElse(0);
  }

  @Override
  public int getDiscountOfActiveTierOfOnGoingCampaign(Long campaignId) {
    validateOngoingCampaign(campaignId);
    return getActiveTier(campaignId).map(CampaignTier::getDiscountPercent).orElse(0);
  }

  @Override
  @Transactional
  public void incrementUnitsCount(Long campaignId, int unitsCount) {
    if (unitsCount <= 0) {
      throw new IllegalArgumentException("Units count must be positive");
    }

    List<CampaignTier> tiers = fetchCampaignTiers(campaignId);
    CampaignTier activeTier = findActiveTier(tiers)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

    int remainingUnits = activeTier.getThresholdQuantity() - activeTier.getCurrentCount();
    updateTierProgress(activeTier, tiers, campaignId, unitsCount, remainingUnits);
  }

  @Override
  public int getTotalDiscountedUnitsOfCampaign(Long campaignId) {
    return campaignTierRepository.findByCampaignId(campaignId).stream()
            .mapToInt(CampaignTier::getThresholdQuantity)
            .sum();
  }

  @Override
  public int getCurrentUnitsCountOfCampaign(Long campaignId) {
    return campaignTierRepository.findByCampaignId(campaignId).stream()
            .mapToInt(CampaignTier::getCurrentCount)
            .sum();
  }

  @Override
  public void validateCampaignTiers(PreorderCampaign campaign) {
    List<CampaignTier> tiers = campaign.getCampaignTiers();
    if (tiers == null || tiers.isEmpty()) {
      throw new IllegalArgumentException("Campaign must have at least one tier.");
    }

    List<CampaignTier> sortedTiers = tiers.stream()
            .sorted(Comparator.comparingInt(CampaignTier::getTierOrder))
            .distinct()
            .toList();

    CampaignType type = campaign.getCampaignType();
    for (int i = 1; i < sortedTiers.size(); i++) {
      CampaignTier current = sortedTiers.get(i);
      CampaignTier previous = sortedTiers.get(i - 1);
      if (type == CampaignType.MILESTONE) {
        if (current.getDiscountPercent() >= previous.getDiscountPercent()) {
          throw new IllegalArgumentException("Milestone tiers must have decreasing discounts.");
        }
      } else if (type == CampaignType.GROUP) {
        if (current.getDiscountPercent() <= previous.getDiscountPercent()) {
          throw new IllegalArgumentException("Group tiers must have increasing discounts.");
        }
      }
    }
  }


  private List<CampaignTier> fetchCampaignTiers(Long campaignId) {
    return campaignTierRepository.findByCampaignId(campaignId);
  }

  private Optional<CampaignTier> findActiveTier(List<CampaignTier> tiers) {
    return tiers.stream()
            .filter(tier -> tier.getTierStatus() == TierStatus.PROCESSING)
            .findFirst();
  }

  private void updateTierProgress(CampaignTier activeTier, List<CampaignTier> tiers, Long campaignId, int unitsCount, int remainingUnits) {
    if (unitsCount >= remainingUnits) {
      completeCurrentTier(activeTier, remainingUnits);
      handleTierCompletion(tiers, activeTier, campaignId, unitsCount - remainingUnits);
    } else {
      activeTier.setCurrentCount(activeTier.getCurrentCount() + unitsCount);
    }
    campaignTierRepository.save(activeTier);
  }

  private void completeCurrentTier(CampaignTier activeTier, int unitsToAdd) {
    activeTier.setCurrentCount(activeTier.getCurrentCount() + unitsToAdd);
    activeTier.setTierStatus(TierStatus.ACHIEVED);
  }

  private void handleTierCompletion(List<CampaignTier> tiers, CampaignTier activeTier, Long campaignId, int excessUnits) {
    if (isLastTier(tiers, activeTier)) {
      endCampaign(campaignId);
    } else {
      CampaignTier nextTier = getNextTier(tiers, activeTier)
              .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
      nextTier.setTierStatus(TierStatus.PROCESSING);
      if (excessUnits > 0) {
        nextTier.setCurrentCount(excessUnits);
        campaignTierRepository.save(nextTier);
      }
    }
  }

  private boolean isLastTier(List<CampaignTier> tiers, CampaignTier tier) {
    return tiers.stream()
            .max(Comparator.comparing(CampaignTier::getTierOrder))
            .map(last -> tier.getTierOrder().equals(last.getTierOrder()))
            .orElse(false);
  }

  private Optional<CampaignTier> getNextTier(List<CampaignTier> tiers, CampaignTier currentTier) {
    return tiers.stream()
            .filter(tier -> tier.getTierOrder().equals(currentTier.getTierOrder() + 1))
            .findFirst();
  }

  @Override
  @Transactional
  public void endCampaign(Long campaignId) {
    PreorderCampaign campaign = preorderCampaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
    campaign.setActive(false);
    preorderCampaignRepository.save(campaign);
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

  private Optional<CampaignTier> getActiveTier(Long campaignId) {
    List<CampaignTier> tiers = fetchCampaignTiers(campaignId);
    return tiers.stream().filter(tier -> tier.getTierStatus() == TierStatus.PROCESSING).findFirst();
  }

  @Override
  public void deleteCampaign(Long id) {
    preorderCampaignRepository.deleteById(id);
  }
}
