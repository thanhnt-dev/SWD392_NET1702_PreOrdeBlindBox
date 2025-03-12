package com.swd392.preOrderBlindBox.repository.repository;

import com.swd392.preOrderBlindBox.entity.Blindbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindboxRepository extends JpaRepository<Blindbox, Long> {
    List<Blindbox> findByBlindboxPackageId(Long packageId);
}
