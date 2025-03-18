package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "blindbox_series_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxSeriesItem extends BaseEntity implements Serializable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "blindbox_series_id", nullable = false)
  private BlindboxSeries blindboxSeries;

  @Column(name = "item_name", length = 50)
  private String itemName;

  @Column(name = "item_chance")
  private float itemChance;

  @OneToMany(mappedBy = "revealedItem")
  private List<Blindbox> blindboxes;
}
