package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindboxSeriesItemRepository extends JpaRepository<BlindboxSeriesItem, Long> {
  List<BlindboxSeriesItem> findByBlindboxSeriesId(Long blindboxSeriesId);
}
