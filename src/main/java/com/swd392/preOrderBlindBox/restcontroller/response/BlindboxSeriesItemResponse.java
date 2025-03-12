package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxSeriesItemResponse {
    private Long id;
    private Long seriesId;
    private String itemName;
    private int itemChance;
    private boolean isActive;
    private List<String> imageUrls;
}
