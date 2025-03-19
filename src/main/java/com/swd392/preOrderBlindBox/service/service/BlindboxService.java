package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.entity.Blindbox;
import com.swd392.preOrderBlindBox.entity.BlindboxPackage;

import java.util.List;

public interface BlindboxService {
  List<Blindbox> createBlindboxesForPackage(int count, BlindboxPackage blindboxPackage);

  Blindbox getBlindboxById(Long id);

  List<Blindbox> getBlindboxesByPackageId(Long packageId);

  void updateBlindboxStatus(Long id, Boolean isSold);

  void updateRevealedItem(Long blindboxId, Long itemId);

  List<Blindbox> getUnsoldBlindboxesOfPackage(Long packageId);

  Blindbox createBlindbox(Blindbox blindbox);
}
