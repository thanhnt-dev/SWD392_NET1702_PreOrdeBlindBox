package com.swd392.preOrderBlindBox.facade.facade;

import com.swd392.preOrderBlindBox.restcontroller.request.LoginRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.RegisterRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.LoginResponse;

public interface UserFacade {
  BaseResponse<LoginResponse> login(LoginRequest request);

  BaseResponse<LoginResponse> register(RegisterRequest request);
}
