package com.swd392.preOrderBlindBox.restcontroller.request;

import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreorderStatusUpdateRequest {
    @NotNull(message = "Preorder ID cannot be null")
    @Positive(message = "Preorder ID must be a positive number")
    private Long id;

    @NotNull(message = "Preorder status cannot be null")
    private PreorderStatus status;
}
