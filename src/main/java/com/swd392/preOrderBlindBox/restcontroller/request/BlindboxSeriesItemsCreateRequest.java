package com.swd392.preOrderBlindBox.restcontroller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxSeriesItemsCreateRequest {
    private Long seriesId;
    private String itemName;
    private int itemChance;
    private MultipartFile itemImage;
}