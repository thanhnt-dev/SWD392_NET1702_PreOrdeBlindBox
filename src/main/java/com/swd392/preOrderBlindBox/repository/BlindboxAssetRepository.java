package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindboxAssetRepository extends JpaRepository<BlindboxAsset, Long> {
    List<BlindboxAsset> findByBlindboxSeriesId(Long blindboxSeriesId);
}
