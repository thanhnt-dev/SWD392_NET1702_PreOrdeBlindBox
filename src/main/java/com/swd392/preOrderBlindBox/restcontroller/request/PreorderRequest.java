package com.swd392.preOrderBlindBox.restcontroller.request;

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
