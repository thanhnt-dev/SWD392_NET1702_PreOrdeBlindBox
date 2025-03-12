package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxResponse {
    private Long id;
    private Long packageId;
    private BlindboxSeriesItemResponse revealedItem;
    private boolean isSold;
    private boolean isActive;
}