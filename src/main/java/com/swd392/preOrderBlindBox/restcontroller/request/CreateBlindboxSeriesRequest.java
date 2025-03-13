package com.swd392.preOrderBlindBox.restcontroller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlindboxSeriesRequest {

  @NotBlank(message = "Series name is required")
  @NotNull(message = "Series name is required")
  private String seriesName;

  private String description;

  @NotNull(message = "Package price is required")
  @DecimalMin(value = "0.01", message = "Package price must be greater than 0")
  private BigDecimal packagePrice;

  @NotNull(message = "Box price is required")
  @DecimalMin(value = "0.01", message = "Box price must be greater than 0")
  private BigDecimal boxPrice;

  @NotNull(message = "Total units required for package")
  private Integer totalUnits;

  @NotEmpty(message = "Items are required")
  private List<BlindboxItemRequest> items;
}
