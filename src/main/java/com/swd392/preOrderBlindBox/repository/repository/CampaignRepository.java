package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.PreorderCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<PreorderCampaign, Long> {
    List<PreorderCampaign> findByBlindboxSeriesId(Long blindboxSeriesId);
//
//    List<PreorderCampaign> findAllByActiveTrue();
//
//    List<PreorderCampaign> findByBlindboxSeriesIdAndActiveTrue(Long blindboxSeriesId);
}
