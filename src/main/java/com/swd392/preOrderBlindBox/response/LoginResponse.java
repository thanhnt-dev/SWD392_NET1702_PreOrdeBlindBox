package com.swd392.preOrderBlindBox.response;

import com.swd392.preOrderBlindBox.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LoginResponse {
  private Long id;
  private String email;
  private String name;
  private String phone;
  private String accessToken;
  private Role roles;
}
