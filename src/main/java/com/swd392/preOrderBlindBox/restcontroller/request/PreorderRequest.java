package com.swd392.preOrderBlindBox.restcontroller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PreorderRequest {
    @NotNull(message = "Phone is required")
    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "The phone number must be 10 digits.")
    @Schema(description = "phoneNumber", example = "0123456789")
    String phoneNumber;

    @NotNull(message = "Address is required")
    @NotBlank(message = "Address cannot be blank")
    @Schema(description = "userAddress", example = "34 Hàng Đào, Hoàn Kiếm, Hà Nội")
    String userAddress;
}
