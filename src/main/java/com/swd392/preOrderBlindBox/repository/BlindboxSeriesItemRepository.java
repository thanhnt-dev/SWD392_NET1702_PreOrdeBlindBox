package com.swd392.preOrderBlindBox.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindboxSeriesItemRepository extends JpaRepository<BlindboxSeriesItem, Long> {
    List<BlindboxSeriesItem> findByBlindboxSeriesId(Long blindboxSeriesId);
}
