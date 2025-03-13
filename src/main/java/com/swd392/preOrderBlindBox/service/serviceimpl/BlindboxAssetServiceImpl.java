package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.BlindboxAsset;
import com.swd392.preOrderBlindBox.repository.repository.BlindboxAssetRepository;
import com.swd392.preOrderBlindBox.service.service.BlindboxAssetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlindboxAssetServiceImpl implements BlindboxAssetService {
  private final BlindboxAssetRepository blindboxAssetRepository;

  @Override
  public List<BlindboxAsset> getAllBlindboxAssets() {
    return blindboxAssetRepository.findAll();
  }

  @Override
  public BlindboxAsset getBlindboxAssetById(Long id) {
    return blindboxAssetRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
  }

  @Override
  public List<BlindboxAsset> getBlindboxAssetsByEntityId(Long entityId) {
    return blindboxAssetRepository.findByEntityId(entityId);
  }

  @Override
  public BlindboxAsset createBlindboxAsset(BlindboxAsset blindboxAsset) {
    return blindboxAssetRepository.save(blindboxAsset);
  }

  @Override
  public BlindboxAsset updateBlindboxAsset(BlindboxAsset blindboxAsset, Long id) {
    BlindboxAsset oldBlindboxAsset = blindboxAssetRepository.findById(id).orElse(null);
    if (oldBlindboxAsset == null) {
      return null;
    }
    oldBlindboxAsset.setMediaKey(blindboxAsset.getMediaKey());
    return blindboxAssetRepository.save(oldBlindboxAsset);
  }

  @Override
  public void deleteBlindboxAsset(Long id) {
    blindboxAssetRepository.deleteById(id);
  }
}
