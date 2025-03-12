package com.swd392.preOrderBlindBox.restcontroller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxSeriesCreateRequest {
    private String seriesName;
    private String description;
    private BigDecimal packagePrice;
    private BigDecimal boxPrice;
    private int numberOfItems;
    private int numberOfSeparatedSalePackages;
    private int numberOfWholeSalePackages;
    private List<MultipartFile> seriesImages;
    private List<BlindboxSeriesItemsCreateRequest> seriesItems;
}