package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    // Changed from findAllByActiveTrue to findAllByIsActiveTrue
    List<Campaign> findAllByIsActiveTrue();

    // Changed from findByBlindboxSeriesId to match entity field name
    List<Campaign> findByBlindboxSeriesId(Long blindboxSeriesId);

    // Changed from findByBlindboxSeriesIdAndActiveTrue to match entity field name
    List<Campaign> findByBlindboxSeriesIdAndIsActiveTrue(Long blindboxSeriesId);
}