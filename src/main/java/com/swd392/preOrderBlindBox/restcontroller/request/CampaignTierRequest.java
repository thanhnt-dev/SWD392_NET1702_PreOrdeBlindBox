package com.swd392.preOrderBlindBox.restcontroller.request;

import com.swd392.preOrderBlindBox.common.enums.TierStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignTierRequest {
  @NotBlank(message = "Alias không được để trống")
  @Size(max = 50, message = "Alias không được quá 50 ký tự")
  private String alias;

  private int currentCount;

  private int thresholdQuantity;

  @Min(value = 1, message = "Tier order phải >= 1")
  private int tierOrder;

  @Min(value = 0, message = "Discount percent phải >= 0")
  @Max(value = 100, message = "Discount percent không được quá 100")
  private int discountPercent;

  @NotNull(message = "Tier status không được để trống")
  private TierStatus tierStatus;
}
