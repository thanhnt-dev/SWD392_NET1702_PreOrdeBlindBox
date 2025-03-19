package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.common.enums.AssetEntityType;
import com.swd392.preOrderBlindBox.entity.BlindboxAsset;
import java.util.List;

public interface BlindboxAssetService {
  List<BlindboxAsset> getAllBlindboxAssets();

  BlindboxAsset getBlindboxAssetById(Long id);

  List<BlindboxAsset> getBlindboxAssetsByEntityIdAndType(Long entityId, AssetEntityType entityType);

  BlindboxAsset createBlindboxAsset(BlindboxAsset blindboxAsset);

  BlindboxAsset updateBlindboxAsset(BlindboxAsset blindboxAsset, Long id);

  void deleteBlindboxAsset(Long id);

  void saveBlindboxAsset(BlindboxAsset blindboxAsset);
}
