package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.ProductType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderItemResponse {
     private BlindboxSeriesResponse blindboxSeries;
     private ProductType productType;
     private int quantity;
     private BigDecimal originalPrice;
     private BigDecimal lockedPrice;
     private List<PreorderItemProductResponse> products;
}
