package com.swd392.preOrderBlindBox.restcontroller.response;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxSeriesItemResponse {
  private Long id;
  private Long seriesId;
  private String itemName;
  private float itemChance;
  private boolean isActive;
  private String imageUrl;
}
