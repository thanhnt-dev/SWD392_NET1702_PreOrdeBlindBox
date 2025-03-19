package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.common.enums.AssetEntityType;
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Entity
@Table(name = "blindbox_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxAsset extends BaseEntity implements Serializable {
  @Column(name = "media_key", nullable = false, length = 100)
  private String mediaKey;

  @Column(name = "entity_id", nullable = false)
  private Long entityId;

  @Enumerated(EnumType.STRING)
  @Column(name = "asset_entity_type", nullable = false)
  private AssetEntityType assetEntityType;
}
