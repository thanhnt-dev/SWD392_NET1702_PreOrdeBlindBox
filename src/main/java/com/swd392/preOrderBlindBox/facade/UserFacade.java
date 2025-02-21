package com.swd392.preOrderBlindBox.facade;

import com.swd392.preOrderBlindBox.request.LoginRequest;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.LoginResponse;

public interface UserFacade {
  BaseResponse<LoginResponse> login(LoginRequest request);
}
