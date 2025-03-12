package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.PackageStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxPackageResponse {
    private Long id;
    private Long seriesId;
    private int totalUnits;
    private int currentSoldUnits;
    private PackageStatus status;
    private boolean isActive;
}