package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Entity
@Table(name = "blindbox_asset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxAsset extends BaseEntity implements Serializable {
  @Column(nullable = false, length = 100)
  private String mediaKey;

  @ManyToOne(
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "blindbox_id", nullable = false)
  private BlindboxSeries blindboxSeries;
}
