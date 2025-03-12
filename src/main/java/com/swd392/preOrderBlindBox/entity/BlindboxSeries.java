package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "blindbox_series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxSeries extends BaseEntity implements Serializable {

  @Column(name = "series_name", nullable = false, length = 50)
  private String seriesName;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "package_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal packagePrice;

  @Column(name = "box_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal boxPrice;

  @OneToMany(mappedBy = "blindboxSeries")
  private List<BlindboxSeriesItem> items;

  @OneToMany(mappedBy = "series")
  private List<BlindboxPackage> packages;

  @OneToMany(mappedBy = "blindboxSeries")
  private List<PreorderCampaign> campaigns;
}
