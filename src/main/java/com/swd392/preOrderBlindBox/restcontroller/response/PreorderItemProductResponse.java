package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderItemProductResponse {
    private Long id;
    private Long seriesId;
    private BlindboxSeriesItemResponse revealedItem;
    private String alias;
}
