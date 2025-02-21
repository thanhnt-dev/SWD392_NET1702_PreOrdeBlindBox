package com.swd392.preOrderBlindBox.facade.Impl;

import com.swd392.preOrderBlindBox.entity.User;
import com.swd392.preOrderBlindBox.enums.ErrorCode;
import com.swd392.preOrderBlindBox.exception.UserException;
import com.swd392.preOrderBlindBox.facade.UserFacade;
import com.swd392.preOrderBlindBox.request.LoginRequest;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.LoginResponse;
import com.swd392.preOrderBlindBox.security.SecurityUserDetails;
import com.swd392.preOrderBlindBox.service.JwtTokenService;
import com.swd392.preOrderBlindBox.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtTokenService jwtService;

  @Override
  public BaseResponse<LoginResponse> login(LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    User user = userService.findByEmail(request.getEmail());

    boolean isNotActive = !user.isActive();
    if (isNotActive) throw new UserException(ErrorCode.USER_IS_DEACTIVATED);

    SecurityUserDetails userPrinciple = (SecurityUserDetails) authentication.getPrincipal();
    return BaseResponse.build(buildLoginResponse(userPrinciple, user), true);
  }

  private LoginResponse buildLoginResponse(SecurityUserDetails userDetails, User user) {
    var accessToken = jwtService.generateToken(userDetails);

    return LoginResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .phone(user.getPhone())
        .name(user.getName())
        .accessToken(accessToken)
        .roles(user.getRoleName())
        .build();
  }
}
