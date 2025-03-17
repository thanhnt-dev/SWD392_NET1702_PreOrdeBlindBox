package com.swd392.preOrderBlindBox.entity;

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

  //  @ManyToOne(fetch = FetchType.LAZY)
  //  @JoinColumn(name = "entity_id", nullable = false)
  //  private BlindboxSeriesItem entityId;
  @Column(name = "entity_id", nullable = false)
  private Long entityId;
}
