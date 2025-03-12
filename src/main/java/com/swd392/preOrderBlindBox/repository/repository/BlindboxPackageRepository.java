package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindboxPackageRepository extends JpaRepository<BlindboxPackage, Long> {
    List<BlindboxPackage> findBySeriesId(Long blindboxSeriesId);
}
