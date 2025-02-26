package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.*;

@Entity
@Table(name = "blindbox_series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxSeries extends BaseEntity implements Serializable {

  @Column(nullable = false, length = 100)
  private String seriesName;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "opened_at", nullable = false)
  private LocalDateTime openedAt;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @OneToMany(mappedBy = "blindboxSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<BlindboxAsset> blindboxAssets = new HashSet<>();

  @OneToMany(mappedBy = "blindboxSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<BlindboxUnit> blindboxUnits = new HashSet<>();

  @OneToMany(mappedBy = "blindboxSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Campaign> campaigns = new ArrayList<>();

  @OneToMany(mappedBy = "blindboxSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
    private Set<BlindboxSeriesItem> blindboxSeriesItems = new HashSet<>();
}
