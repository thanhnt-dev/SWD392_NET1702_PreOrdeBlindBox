package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Entity
@Table(name = "blindboxes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blindbox extends BaseEntity implements Serializable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blindbox_package_id")
  private BlindboxPackage blindboxPackage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "revealed_item_id")
  private BlindboxSeriesItem revealedItem;

  @Column(name = "is_sold", nullable = false)
  private Boolean isSold = false;
}
