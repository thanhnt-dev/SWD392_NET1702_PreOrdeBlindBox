package com.swd392.preOrderBlindBox.facade.facade;

import com.swd392.preOrderBlindBox.restcontroller.request.LoginRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.RegisterRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.UserCriteria;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.LoginResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaginationResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.UserInfoResponse;
import java.util.List;

public interface UserFacade {
  BaseResponse<LoginResponse> login(LoginRequest request);

  BaseResponse<LoginResponse> register(RegisterRequest request);

  BaseResponse<Void> setUserAsStaff(Long id);

  BaseResponse<Void> updateUserActiveStatus(Long id, boolean isActive);

  BaseResponse<PaginationResponse<List<UserInfoResponse>>> getUserByFilter(UserCriteria criteria);
}
