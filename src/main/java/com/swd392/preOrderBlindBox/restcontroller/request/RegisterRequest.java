package com.swd392.preOrderBlindBox.restcontroller.request;

import com.swd392.preOrderBlindBox.common.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RegisterRequest {
  @NotNull(message = "Email is required")
  @NotBlank(message = "Email cannot be blank")
  @Pattern(
      regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
      message = "The email format is incorrect.")
  @Schema(description = "email", example = "email@email.com")
  private String email;

  @NotNull(message = "Name is required")
  @NotBlank(message = "Name cannot be blank")
  private String name;

  private Gender gender;

  @NotNull(message = "Phone is required")
  @NotBlank(message = "Phone cannot be blank")
  @Pattern(regexp = "^[0-9]{10}$", message = "The phone number must be 10 digits.")
  private String phone;

  private LocalDate dateOfBirth;

  @NotNull(message = "Password is required")
  @NotBlank(message = "Password cannot be blank")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
      message =
          "The password must be at least 8 characters, including letters, numbers, and special characters.")
  @Schema(description = "password", example = "NguyenThanhSr4@")
  private String password;
}
