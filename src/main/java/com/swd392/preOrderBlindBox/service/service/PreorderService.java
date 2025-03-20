package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import com.swd392.preOrderBlindBox.entity.Preorder;
import com.swd392.preOrderBlindBox.entity.PreorderItem;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderDetailsManagementResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderDetailsResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreordersHistoryResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PreorderService {
    Preorder createPreorder(Preorder preorder);

    Optional<Preorder> getPreorderById(Long id);

    Optional<Preorder> getPreorderByOrderCode(String orderCode);

    PreorderDetailsManagementResponse updatePreorderStatus(Long id, PreorderStatus status);

    BigDecimal calculateDepositAmount(BigDecimal price);

    BigDecimal calculateRemainingAmount(BigDecimal price);

    void assignBlindboxProductToPreorderItem(Long preorderId);

    void updatePreorder(Preorder preorder);

    List<PreordersHistoryResponse> getPreordersOfUser();

    PreorderDetailsResponse getPreorderDetails(Long preorderId);

    List<PreorderItem> getPreorderItemsAssociatedWithBlindboxSeries(Long seriesId);

    void updatePreorderItem(PreorderItem preorderItem);

    void updatePreorderPrice(Long preorderId);

    void updatePreorderTotalAmount(Long preorderId);

    void updatePreorderEstimatedTotalAmount(Long preorderId);

    List<PreordersHistoryResponse> getAllPreorders();

    PreorderDetailsManagementResponse getPreorderDetailsManagement(Long preorderId);
}
