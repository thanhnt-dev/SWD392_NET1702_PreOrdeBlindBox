package com.swd392.preOrderBlindBox.restcontroller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlindboxItemRequest {
  @NotBlank(message = "Item name is required")
  @NotNull(message = "Item name is required")
  private String itemName;

  @NotNull(message = "Item chance is required")
  private Integer itemChance;
}
