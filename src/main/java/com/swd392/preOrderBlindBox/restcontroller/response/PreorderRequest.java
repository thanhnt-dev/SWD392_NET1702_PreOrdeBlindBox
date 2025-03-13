package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderRequest {
    String phoneNumber;
    String userAddress;
}
