package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.CampaignTier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignTierRepository extends JpaRepository<CampaignTier, Long> {
  List<CampaignTier> findByCampaignId(Long campaignId);
}
