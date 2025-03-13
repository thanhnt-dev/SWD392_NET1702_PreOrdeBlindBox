package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreorderCampaignRepository extends JpaRepository<PreorderCampaign, Long> {
  List<PreorderCampaign> findByBlindboxSeriesId(Long blindboxSeriesId);
}
