package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.facade.facade.UserFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.LoginRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.RegisterRequest;
import com.swd392.preOrderBlindBox.restcontroller.request.UserCriteria;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.LoginResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PaginationResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Sign up account",
      tags = {"Account APIs"})
  public BaseResponse<LoginResponse> signUp(@Validated @RequestBody RegisterRequest request) {
    return this.userFacade.register(request);
  }

  @PutMapping("/{id}/set-staff")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(
      summary = "Set Role Staff for User",
      tags = {"Account APIs"})
  public BaseResponse<Void> setUserAsStaff(@PathVariable Long id) {
    return this.userFacade.setUserAsStaff(id);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(
      summary = "Get all users with filter",
      tags = {"Account APIs"})
  public BaseResponse<PaginationResponse<List<UserInfoResponse>>> getAllUser(
      @RequestBody UserCriteria criteria) {
    return this.userFacade.getUserByFilter(criteria);
  }

  @PutMapping("/{id}/active-status")
  @ResponseStatus(HttpStatus.OK)
  //  @PreAuthorize("hasRole('ADMIN')")
  //  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(
      summary = "Set active or deactivate user",
      tags = {"Account APIs"})
  public BaseResponse<Void> updateUserActiveStatus(
      @PathVariable Long id, @RequestParam boolean isActive) {
    return this.userFacade.updateUserActiveStatus(id, isActive);
  }
}
