package com.swd392.preOrderBlindBox.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  USER_NOT_FOUND("1000", "Can not find user with email"),
  USER_IS_DEACTIVATED("1001", "Your account is deactivated"),
  BAD_CREDENTIAL_LOGIN("1002", "Invalid username or password");

  private final String code;
  private final String message;
}
