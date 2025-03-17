package com.swd392.preOrderBlindBox.restcontroller.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentResponse {
  public String paymentUrl;
}
