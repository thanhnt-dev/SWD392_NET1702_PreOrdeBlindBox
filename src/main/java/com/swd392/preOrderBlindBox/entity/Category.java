package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity implements Serializable {

  @Column(nullable = false, length = 50)
  private String categoryName;

  @ManyToOne
  @JoinColumn(name = "parent_cate_id")
  private Category parentCategory;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  List<BlindboxSeries> blindboxSeries = new ArrayList<>();
}
