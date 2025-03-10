package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.CampaignTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignTierRepository extends JpaRepository<CampaignTier, Long> {
    List<CampaignTier> findByCampaignId(Long campaignId);
}
