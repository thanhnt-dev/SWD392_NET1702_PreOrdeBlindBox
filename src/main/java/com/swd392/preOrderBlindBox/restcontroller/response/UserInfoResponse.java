package com.swd392.preOrderBlindBox.restcontroller.response;

import com.swd392.preOrderBlindBox.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserInfoResponse {
  private Long id;
  private String email;
  private String phone;
  private String name;
  private boolean isActive;
  private Long createdAt;
  private Long updatedAt;
  private Role role;
}
