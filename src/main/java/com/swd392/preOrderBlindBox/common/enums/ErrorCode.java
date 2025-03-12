package com.swd392.preOrderBlindBox.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  USER_NOT_FOUND("1000", "Can not find user with email"),
  USER_IS_DEACTIVATED("1001", "Your account is deactivated"),
  BAD_CREDENTIAL_LOGIN("1002", "Invalid username or password"),
  RESOURCES_NOT_FOUND("1003", "Can not find resources"),
  UNAUTHORIZED_CART_ACCESS("1004", "Cannot access cart that doesn't belong to current user");

  private final String code;
  private final String message;
}
