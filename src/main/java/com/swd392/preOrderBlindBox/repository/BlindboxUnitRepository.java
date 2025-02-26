package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindboxUnitRepository extends JpaRepository<BlindboxUnit, Long> {
    List<BlindboxUnit> findByBlindboxSeriesId(Long blindboxSeriesId);
}
