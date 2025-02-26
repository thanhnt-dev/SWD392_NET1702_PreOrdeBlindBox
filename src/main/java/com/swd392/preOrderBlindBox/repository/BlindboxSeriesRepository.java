package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindboxSeriesRepository extends JpaRepository<BlindboxSeries, Long>, JpaSpecificationExecutor<BlindboxSeries> {
}