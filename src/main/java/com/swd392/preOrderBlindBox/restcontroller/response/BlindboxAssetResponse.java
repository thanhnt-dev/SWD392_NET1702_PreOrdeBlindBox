package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BlindboxAssetResponse {
    private Long id;
    private Long entityId;
    private String mediaUrl;
    private boolean isActive;
}
