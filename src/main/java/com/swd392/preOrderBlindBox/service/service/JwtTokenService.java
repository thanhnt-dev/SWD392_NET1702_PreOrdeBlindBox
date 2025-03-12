package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.infrastructure.security.SecurityUserDetails;

public interface JwtTokenService {
  String generateToken(SecurityUserDetails user);

  Boolean validateToken(String token);

  String getEmailFromJwtToken(String token);
}
