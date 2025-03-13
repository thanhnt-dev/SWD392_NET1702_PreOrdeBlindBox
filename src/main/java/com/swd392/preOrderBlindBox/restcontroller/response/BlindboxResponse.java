package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxResponse {
  private Long id;
  private Long packageId;
  private BlindboxSeriesItemResponse revealedItem;
  private boolean isSold;
  private boolean isActive;
}
