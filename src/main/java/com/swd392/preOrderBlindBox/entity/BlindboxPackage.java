package com.swd392.preOrderBlindBox.entity;

import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "blindbox_unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxPackage extends BaseEntity implements Serializable {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "series_id")
  private BlindboxSeries series;

  @Column(name = "total_units", nullable = false)
  private Integer totalUnits;

  @Column(name = "current_sold_units", nullable = false)
  private Integer currentSoldUnits;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PackageStatus status;

  @OneToMany(mappedBy = "blindboxPackage")
  private List<Blindbox> blindboxes;
}
