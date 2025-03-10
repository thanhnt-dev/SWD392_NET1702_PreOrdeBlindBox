package com.swd392.preOrderBlindBox.response;

import com.swd392.preOrderBlindBox.entity.BlindboxAsset;
import com.swd392.preOrderBlindBox.entity.BlindboxSeriesItem;
import com.swd392.preOrderBlindBox.entity.BlindboxUnit;
import com.swd392.preOrderBlindBox.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxSeriesResponse {
    private Long id;
    private String seriesName;
    private LocalDateTime openedAt;
    private String description;
    private CategoryResponse category;

}
