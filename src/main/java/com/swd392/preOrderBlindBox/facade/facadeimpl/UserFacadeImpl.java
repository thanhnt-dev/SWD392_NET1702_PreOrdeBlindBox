package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.enums.Role;
import com.swd392.preOrderBlindBox.common.exception.UserException;
import com.swd392.preOrderBlindBox.entity.User;
import com.swd392.preOrderBlindBox.facade.facade.UserFacade;
import com.swd392.preOrderBlindBox.infrastructure.security.SecurityUserDetails;
import com.swd392.preOrderBlindBox.restcontroller.request.LoginRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.RegisterRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.LoginResponse;
import com.swd392.preOrderBlindBox.service.service.JwtTokenService;
import com.swd392.preOrderBlindBox.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtTokenService jwtService;
  private final PasswordEncoder passwordEncoder;

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

  @Override
  public BaseResponse<LoginResponse> register(RegisterRequest request) {
    userService.validateSignUp(request);
    var user =
        User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .name(request.getName())
            .gender(request.getGender())
            .dateOfBirth(request.getDateOfBirth())
            .roleName(Role.USER)
            .build();
    User createUser = userService.createUser(user);

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(createUser.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    SecurityUserDetails userPrinciple = (SecurityUserDetails) authentication.getPrincipal();
    return BaseResponse.build(buildLoginResponse(userPrinciple, createUser), true);
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
