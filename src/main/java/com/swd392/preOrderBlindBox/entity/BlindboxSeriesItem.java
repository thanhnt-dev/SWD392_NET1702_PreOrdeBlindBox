package com.swd392.preOrderBlindBox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "blindbox_series_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindboxSeriesItem extends BaseEntity implements Serializable {

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "rarity_percentage", precision = 5, scale = 2)
    private BigDecimal rarityPercentage;

    @ManyToOne(
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "blindbox_series_id", nullable = false)
    private BlindboxSeries blindboxSeries;
}
