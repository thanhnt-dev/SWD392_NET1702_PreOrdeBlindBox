package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.BlindboxAsset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindboxAssetRepository extends JpaRepository<BlindboxAsset, Long> {
  List<BlindboxAsset> findByEntityId(Long id);
}
