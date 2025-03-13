package com.swd392.preOrderBlindBox.restcontroller.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
