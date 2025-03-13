package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.CampaignType;
import com.swd392.preOrderBlindBox.common.enums.PreorderStatus;
import com.swd392.preOrderBlindBox.entity.CartItem;
import com.swd392.preOrderBlindBox.entity.Preorder;
import com.swd392.preOrderBlindBox.entity.PreorderItem;
import com.swd392.preOrderBlindBox.repository.repository.PreorderItemRepository;
import com.swd392.preOrderBlindBox.repository.repository.PreorderRepository;
import com.swd392.preOrderBlindBox.service.service.CartService;
import com.swd392.preOrderBlindBox.service.service.PreorderCampaignService;
import com.swd392.preOrderBlindBox.service.service.PreorderService;
import com.swd392.preOrderBlindBox.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
@Transactional
public class PreorderServiceImpl implements PreorderService {
    private final PreorderRepository preorderRepository;
    private final PreorderItemRepository preorderItemRepository;
    private final CartService cartService;
    private final PreorderCampaignService preorderCampaignService;
    private final UserService userService;

    @Override
    public Preorder createPreorder(Preorder preorderRequest) {
        Long userId = userService.getCurrentUser().isPresent() ? userService.getCurrentUser().get().getId() : null;
        List<CartItem> cartItems = cartService.getCartItems(userId);
        List<PreorderItem> preorderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            PreorderItem preorderItem = getPreorderItem(preorderRequest, cartItem);
            preorderItems.add(preorderItem);
        }

        preorderRequest.setOrderCode(generateOrderCode());
        preorderRequest.setPreorderItems(preorderItems);
        preorderRequest.setTotalPrice(calculateFullPaymentAmount(preorderRequest.getId()));
        preorderRequest.setPreorderStatus(PreorderStatus.DEPOSIT_PENDING);

        cartService.clearCart(userId);

        return preorderRepository.save(preorderRequest);
    }

    private static PreorderItem getPreorderItem(Preorder preorderRequest, CartItem cartItem) {
        BigDecimal itemTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        if (cartItem.getDiscountPercent() > 0) {
            BigDecimal discountFactor = BigDecimal.ONE.subtract(
                    BigDecimal.valueOf(cartItem.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            itemTotal = itemTotal.multiply(discountFactor);
        }

        PreorderItem.PreorderItemBuilder builder = PreorderItem.builder()
                .originalPrice(itemTotal)
                .preorder(preorderRequest)
                .blindboxSeries(cartItem.getSeries())
                .itemFromCampaignType(cartItem.getItemCampaignType())
                .productType(cartItem.getProductType());

        if (cartItem.getItemCampaignType() == CampaignType.MILESTONE) {
            builder.lockedPrice(itemTotal);
        }

        return builder.build();
    }

    @Override
    public Optional<Preorder> getPreorderById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Preorder> getPreorderByOrderCode(String orderCode) {
        return Optional.empty();
    }

    @Override
    public Preorder updatePreorder(Preorder preorder) {
        return null;
    }

    @Override
    public Preorder updatePreorderStatus(Long id, PreorderStatus status) {
        return null;
    }

    @Override
    public BigDecimal calculateDepositAmount(Long preorderId) {
        return null;
    }

    @Override
    public BigDecimal calculateFullPaymentAmount(Long preorderId) {
        List<PreorderItem> preorderItems = preorderItemRepository.findByPreorderId(preorderId);

        BigDecimal totalAmount = preorderItems.stream()
                .map(preorderItem -> preorderItem.getItemFromCampaignType() == CampaignType.MILESTONE ?
                        preorderItem.getLockedPrice() : preorderItem.getOriginalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void assignBlindboxProductToPreorderItem(Long preorderId) {

    }

    private String generateOrderCode() {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();

        String timestamp = String.valueOf(System.currentTimeMillis());

        code.append("PO");

        code.append(timestamp.substring(Math.max(0, timestamp.length() - 8)));

        java.util.Random random = new java.util.Random();
        while (code.length() < 20) {
            code.append(alphanumeric.charAt(random.nextInt(alphanumeric.length())));
        }

        return code.toString();
    }
}
