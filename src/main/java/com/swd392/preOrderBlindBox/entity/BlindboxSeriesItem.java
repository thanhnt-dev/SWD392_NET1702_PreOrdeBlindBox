package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "blindbox_series_item")
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
    private Integer itemChance;

    @OneToMany(mappedBy = "revealedItem")
    private List<Blindbox> blindboxes;
}
