package com.swd392.preOrderBlindBox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OtpMailDTO {
  private String receiverMail;
  private String otpCode;
}
