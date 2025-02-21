package com.swd392.preOrderBlindBox.service;

import com.swd392.preOrderBlindBox.security.SecurityUserDetails;

public interface JwtTokenService {
  String generateToken(SecurityUserDetails user);

  Boolean validateToken(String token);

  String getEmailFromJwtToken(String token);
}
