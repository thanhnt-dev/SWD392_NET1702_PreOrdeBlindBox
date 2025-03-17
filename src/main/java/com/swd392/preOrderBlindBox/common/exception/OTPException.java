package com.swd392.preOrderBlindBox.common.exception;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OTPException extends RuntimeException {
  private final String errorCode;
  private final String message;

  public OTPException(ErrorCode errorCode) {
    super(errorCode.name());
    this.errorCode = errorCode.getCode();
    this.message = errorCode.getMessage();
  }
}
