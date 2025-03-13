package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.Blindbox;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindboxRepository extends JpaRepository<Blindbox, Long> {
  List<Blindbox> findByBlindboxPackageId(Long packageId);
}
