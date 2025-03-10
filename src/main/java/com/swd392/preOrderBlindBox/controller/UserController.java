package com.swd392.preOrderBlindBox.controller;

import com.swd392.preOrderBlindBox.entity.User;
import com.swd392.preOrderBlindBox.enums.Gender;
import com.swd392.preOrderBlindBox.enums.Role;
import com.swd392.preOrderBlindBox.facade.UserFacade;
import com.swd392.preOrderBlindBox.repository.UserRepository;
import com.swd392.preOrderBlindBox.request.LoginRequest;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.LoginResponse;
import com.swd392.preOrderBlindBox.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserFacade userFacade;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
          summary = "Login account",
          tags = {"Account APIs"})
  public BaseResponse<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
    return this.userFacade.login(request);
  }

  @GetMapping("/create-test-user")
  public String createTestUser() {
    try {
      User user = new User();
      user.setEmail("test@example.com");
      user.setPassword(passwordEncoder.encode("Test123@"));
      user.setName("Test User");
      user.setPhone("0987654321");
      user.setRoleName(Role.USER); // Giả sử Role là một enum
      user.setGender(Gender.MALE); // Giả sử Gender là một enum

      user.setDateOfBirth(LocalDate.of(2000, 1, 1)); // năm, tháng, ngày



      userRepository.save(user);
      return "Test user created successfully";
    } catch (Exception e) {
      return "Error creating test user: " + e.getMessage();
    }
  }
}