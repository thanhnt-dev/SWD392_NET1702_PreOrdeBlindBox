package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxSeriesDetailsResponse {
    private Long id;
    private String seriesName;
    private String description;
    private BigDecimal packagePrice;
    private BigDecimal boxPrice;
    private boolean isActive;
    private List<String> seriesImageUrls;
    private int availablePackageUnits;
    private int availableBoxUnits;
    private List<BlindboxSeriesItemResponse> items;
    private PreorderCampaignDetailsResponse activeCampaign;
}
