package com.swd392.preOrderBlindBox.restcontroller.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxSeriesResponse {
  private Long id;
  private String seriesName;
  private String description;
  private BigDecimal packagePrice;
  private BigDecimal boxPrice;
  private boolean isActive;
  private List<String> seriesImageUrls;
}
