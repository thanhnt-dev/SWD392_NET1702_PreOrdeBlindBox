package com.swd392.preOrderBlindBox.facade.facadeimpl;

import com.swd392.preOrderBlindBox.common.enums.ErrorCode;
import com.swd392.preOrderBlindBox.common.enums.Role;
import com.swd392.preOrderBlindBox.common.exception.OTPException;
import com.swd392.preOrderBlindBox.common.exception.UserException;
import com.swd392.preOrderBlindBox.dto.OtpMailDTO;
import com.swd392.preOrderBlindBox.entity.User;
import com.swd392.preOrderBlindBox.facade.facade.UserFacade;
import com.swd392.preOrderBlindBox.infrastructure.security.SecurityUserDetails;
import com.swd392.preOrderBlindBox.restcontroller.request.*;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.LoginResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaginationResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.UserInfoResponse;
import com.swd392.preOrderBlindBox.service.service.CacheService;
import com.swd392.preOrderBlindBox.service.service.JwtTokenService;
import com.swd392.preOrderBlindBox.service.service.MailQueueProducer;
import com.swd392.preOrderBlindBox.service.service.UserService;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
  private final CacheService cacheService;
  private final MailQueueProducer mailQueueProducer;

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

  @Override
  public BaseResponse<Void> setUserAsStaff(Long id) {
    User user = userService.findById(id);
    user.setRoleName(Role.STAFF);
    userService.updateUser(user);
    return BaseResponse.ok();
  }

  @Override
  public BaseResponse<Void> updateUserActiveStatus(Long id, boolean isActive) {
    User user = userService.findById(id);
    user.setActive(isActive);
    userService.updateUser(user);
    return BaseResponse.ok();
  }

  @Override
  public BaseResponse<PaginationResponse<List<UserInfoResponse>>> getUserByFilter(
      UserCriteria criteria) {
    var users = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    boolean isAdmin = users.getRoleName().equals(Role.ADMIN);
    var result = userService.findByFilter(criteria, isAdmin);
    List<UserInfoResponse> responses =
        result.getContent().stream().map(this::buildUserInfoResponse).toList();
    return BaseResponse.build(
        PaginationResponse.build(responses, result, criteria.getCurrentPage()), true);
  }

  @Override
  public BaseResponse<Void> forgotPassword(ForgotPasswordRequest request) {
    User user = userService.findByEmail(request.getEmail());
    String cacheKey = String.format("%s-%s", "FORGOT_PASSWORD", request.getEmail());

    boolean isKeyExist = cacheService.hasKey(cacheKey);
    if (isKeyExist) cacheService.delete(cacheKey);
    sendOTP(request.getEmail());
    return BaseResponse.ok();
  }

  @Override
  public void resendOTP(ForgotPasswordRequest request) {
    String cacheKey = String.format("%s-%s", "FORGOT_PASSWORD", request.getEmail());

    boolean isKeyExist = cacheService.hasKey(cacheKey);
    if (isKeyExist) cacheService.delete(cacheKey);
    sendOTP(request.getEmail());
  }

  @Override
  public void confirmOTP(ConfirmOTPRequest request) {
    String cacheKey = String.format("%s-%s", "FORGOT_PASSWORD", request.getEmail());
    String cachedValue = (String) cacheService.retrieve(cacheKey);
    if (null == cachedValue) throw new OTPException(ErrorCode.OTP_INVALID_OR_EXPIRED);
    boolean isValidOTP = cachedValue.equals(request.getOtpCode());
    if (!isValidOTP) throw new OTPException(ErrorCode.OTP_NOT_MATCH);

    cacheService.delete(cacheKey);
  }

  private String generateOtp() {
    Random random = new Random();
    int otp = random.nextInt(999999);
    return String.format("%06d", otp);
  }

  private void sendOTP(String receiverMail) {
    String otp = generateOtp();
    String cacheKey = String.format("%s-%s", "FORGOT_PASSWORD", receiverMail);

    cacheService.store(cacheKey, otp, 5, TimeUnit.MINUTES);
    mailQueueProducer.sendMailMessage(
        OtpMailDTO.builder().receiverMail(receiverMail).otpCode(otp).build());
  }

  private UserInfoResponse buildUserInfoResponse(User user) {
    return UserInfoResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .phone(user.getPhone())
        .name(user.getName())
        .build();
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
