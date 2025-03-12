package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BlindboxAssetResponse {
    private Long id;
    private Long entityId;
    private String mediaUrl;
    private boolean isActive;
}
