package com.swd392.preOrderBlindBox.facade.facade;


import com.swd392.preOrderBlindBox.common.enums.TransactionType;
import com.swd392.preOrderBlindBox.entity.Preorder;

public interface CheckoutFacade {
    Preorder createPreorder(Preorder preorder);

    String initiatePayment(Long preorderId, TransactionType transactionType, boolean isDeposit);

    boolean finalizePayment(String transactionCode, boolean success);

}
