package com.swd392.preOrderBlindBox.common.exception;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InvalidTokenException extends RuntimeException {
  private final String errorCode;
  private final String message;

  public InvalidTokenException(ErrorCode errorCode) {
    super(errorCode.name());
    this.errorCode = errorCode.getCode();
    this.message = errorCode.getMessage();
  }
}
