package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import com.swd392.preOrderBlindBox.entity.Preorder;

import java.math.BigDecimal;
import java.util.Optional;

public interface PreorderService {
    Preorder createPreorder(Preorder preorder);

    Optional<Preorder> getPreorderById(Long id);

    Optional<Preorder> getPreorderByOrderCode(String orderCode);

    Preorder updatePreorderStatus(Long id, PreorderStatus status);

    BigDecimal calculateDepositAmount(BigDecimal price);

    BigDecimal calculateRemainingAmount(BigDecimal price);

    void assignBlindboxProductToPreorderItem(Long preorderId);

    void updatePreorder(Preorder preorder);
}
