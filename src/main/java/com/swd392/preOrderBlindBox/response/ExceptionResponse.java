package com.swd392.preOrderBlindBox.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExceptionResponse {
  private final String code;
  private final String message;
}
