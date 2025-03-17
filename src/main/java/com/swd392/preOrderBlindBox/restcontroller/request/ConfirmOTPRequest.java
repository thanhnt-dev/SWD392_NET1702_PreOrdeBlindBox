package com.swd392.preOrderBlindBox.restcontroller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOTPRequest {
  @NotNull(message = "Email is require")
  @NotBlank(message = "Email can not be blank")
  @Pattern(
      regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
      message = "The email format is incorrect.")
  @Schema(description = "example", example = "example@email.com")
  private String email;

  @NotNull(message = "OTP is require")
  @NotBlank(message = "OTP can not be blank")
  @Schema(description = "OTP Code", example = "123456")
  private String otpCode;
}
