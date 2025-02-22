package com.swd392.preOrderBlindBox.controller;

import com.swd392.preOrderBlindBox.facade.UserFacade;
import com.swd392.preOrderBlindBox.request.LoginRequest;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserFacade userFacade;

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Login account",
      tags = {"Account APIs"})
  public BaseResponse<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
    return this.userFacade.login(request);
  }
}
