package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.*;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.common.util.Util;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.repository.repository.PreorderItemRepository;
import com.swd392.preOrderBlindBox.repository.repository.PreorderRepository;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderItemEstimateResponse;
import com.swd392.preOrderBlindBox.service.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
@Transactional
public class PreorderServiceImpl implements PreorderService {
    private final PreorderRepository preorderRepository;
    private final PreorderItemRepository preorderItemRepository;
    private final CartService cartService;
    private final UserService userService;
    private final BlindboxPackageService blindboxPackageService;
    private final BlindboxService blindboxService;
    @Value("${deposit.rate:0.5}")
    private BigDecimal depositRate;

    @PostConstruct
    public void init() {
        if (depositRate == null || depositRate.compareTo(BigDecimal.ZERO) <= 0 || depositRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Invalid deposit.rate: " + depositRate + ". Must be between 0 and 1.");
        }
    }

    @Override
    public Preorder createPreorder(Preorder preorderRequest) {
        Cart cart = cartService.getOrCreateCart();
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        preorderRequest.setUser(userService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND)));
        preorderRequest.setOrderCode(generateOrderCode());
        preorderRequest.setPreorderStatus(PreorderStatus.PENDING);

        BigDecimal estimatedTotalAmount = cartService.calculateCartTotal();
        BigDecimal depositAmount = Util.calculatePriceWithCoefficient(estimatedTotalAmount, depositRate);
        BigDecimal remainingAmount = null;
        BigDecimal totalAmount = null;

        if (!hasCampaignItemOfTypeGroup(cart)) {
            totalAmount = estimatedTotalAmount;
            remainingAmount = Util.normalizePrice(totalAmount.subtract(depositAmount));
        }

        preorderRequest.setEstimatedTotalAmount(estimatedTotalAmount);
        preorderRequest.setTotalAmount(totalAmount);
        preorderRequest.setDepositAmount(depositAmount);
        preorderRequest.setRemainingAmount(remainingAmount);

        Preorder savedPreorder = preorderRepository.save(preorderRequest);

        List<PreorderItem> preorderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            PreorderItem preorderItem = getPreorderItem(savedPreorder, cartItem);
            preorderItems.add(preorderItem);
        }

        preorderItemRepository.saveAll(preorderItems);

        savedPreorder.setPreorderItems(preorderItems);
        cartService.clearCart();

        return savedPreorder;
    }

    private boolean hasCampaignItemOfTypeGroup(Cart cart) {
        return cart.getCartItems().stream()
                .anyMatch(item -> item.getItemCampaignType() == CampaignType.GROUP);
    }

    private PreorderItem getPreorderItem(Preorder preorderRequest, CartItem cartItem) {
        PreorderItem.PreorderItemBuilder builder = PreorderItem.builder()
                .originalPrice(cartService.calculateItemTotal(cartItem))
                .preorder(preorderRequest)
                .blindboxSeries(cartItem.getSeries())
                .itemFromCampaignType(cartItem.getItemCampaignType())
                .quantity(cartItem.getQuantity())
                .productType(cartItem.getProductType());

        if (cartItem.getItemCampaignType() == CampaignType.MILESTONE || cartItem.getItemCampaignType() == null) {
            builder.lockedPrice(cartService.calculateItemTotal(cartItem));
        }

        return builder.build();
    }

    @Override
    public Optional<Preorder> getPreorderById(Long id) {
        return preorderRepository.findById(id);
    }

    @Override
    public Optional<Preorder> getPreorderByOrderCode(String orderCode) {
        return preorderRepository.findByOrderCode(orderCode);
    }

    @Override
    public Preorder updatePreorderStatus(Long id, PreorderStatus status) {
        Preorder preorder = preorderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        preorder.setPreorderStatus(status);
        return preorderRepository.save(preorder);
    }

    @Override
    public BigDecimal calculateDepositAmount(BigDecimal price) {
        return Util.calculatePriceWithCoefficient(price, depositRate);
    }

    @Override
    public BigDecimal calculateRemainingAmount(BigDecimal price) {
        BigDecimal deposit = calculateDepositAmount(price);
        return Util.normalizePrice(price.subtract(deposit));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignBlindboxProductToPreorderItem(Long preorderId) {
        Preorder preorder = preorderRepository.findById(preorderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        for (PreorderItem preorderItem : preorder.getPreorderItems()) {
            assignProductsToPreorderItem(preorderItem);
        }

        preorderRepository.save(preorder);
    }

    @Override
    public void updatePreorder(Preorder preorder) {
        if (preorder == null || preorder.getId() == null) {
            throw new IllegalArgumentException("Cannot update null preorder or preorder without ID");
        }

        Preorder existingPreorder = preorderRepository.findById(preorder.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));


        existingPreorder.setPreorderStatus(preorder.getPreorderStatus());
        existingPreorder.setTotalAmount(preorder.getTotalAmount());
        existingPreorder.setDepositAmount(preorder.getDepositAmount());
        existingPreorder.setRemainingAmount(preorder.getRemainingAmount());

        if (preorder.getPreorderItems() != null) {
            for (PreorderItem updatedItem : preorder.getPreorderItems()) {
                if (updatedItem.getId() != null) {
                    PreorderItem existingItem = preorderItemRepository.findById(updatedItem.getId())
                            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

                    existingItem.setQuantity(updatedItem.getQuantity());
                    existingItem.setOriginalPrice(updatedItem.getOriginalPrice());
                    existingItem.setLockedPrice(updatedItem.getLockedPrice());
                    existingItem.setProductIds(updatedItem.getProductIds());
                } else {
                    updatedItem.setPreorder(existingPreorder);
                    preorderItemRepository.save(updatedItem);
                }
            }
        }

        preorderRepository.save(existingPreorder);
    }

    private void assignProductsToPreorderItem(PreorderItem preorderItem) {
        List<Blindbox> blindboxesPool = new ArrayList<>(getBlindboxesPool(preorderItem.getBlindboxSeries().getId()));
        List<BlindboxPackage> wholeSalePackages = new ArrayList<>(blindboxPackageService.getPackagesForWholeSaleOfSeries(preorderItem.getBlindboxSeries().getId()));

        int quantity = preorderItem.getQuantity();
        ProductType productType = preorderItem.getProductType();

        if (productType == ProductType.BOX && blindboxesPool.size() < quantity) {
            throw new IllegalStateException("Not enough blindboxes available for assignment");
        } else if (productType == ProductType.PACKAGE && wholeSalePackages.size() < quantity) {
            throw new IllegalStateException("Not enough packages available for assignment");
        }

        List<Long> productIdsList = new ArrayList<>(); // Collect IDs in a list
        for (int i = 0; i < quantity; i++) {
            Long productId = selectProduct(productType, blindboxesPool, wholeSalePackages);
            productIdsList.add(productId);
            // Remove the selected product from the pool to avoid reassignment
            if (productType == ProductType.BOX) {
                blindboxesPool.removeIf(b -> b.getId().equals(productId));
            } else if (productType == ProductType.PACKAGE) {
                wholeSalePackages.removeIf(p -> p.getId().equals(productId));
            }
        }

        preorderItem.setProductIds(String.join(",", productIdsList.stream().map(String::valueOf).toList()));
    }

    private List<Blindbox> getBlindboxesPool(Long seriesId) {
        List<Blindbox> blindboxesPool = new ArrayList<>();
        List<BlindboxPackage> separatedPackages = blindboxPackageService.getPackagesForSeparatedSaleOfSeries(seriesId);

        for (BlindboxPackage blindboxPackage : separatedPackages) {
            blindboxesPool.addAll(blindboxService.getUnsoldBlindboxesOfPackage(blindboxPackage.getId()));
        }

        return blindboxesPool;
    }

    private Long selectProduct(ProductType productType, List<Blindbox> blindboxes, List<BlindboxPackage> wholeSalePackages) {
        Random random = new Random();

        if (productType == ProductType.BOX && !blindboxes.isEmpty()) {
            Long boxId = blindboxes.get(random.nextInt(blindboxes.size())).getId();
            Blindbox blindbox = blindboxService.getBlindboxById(boxId);
            blindbox.setIsSold(true);
            return boxId;
        } else if (productType == ProductType.PACKAGE && !wholeSalePackages.isEmpty()) {
            Long packageId = wholeSalePackages.get(random.nextInt(wholeSalePackages.size())).getId();
            BlindboxPackage blindboxPackage = blindboxPackageService.getBlindboxPackageById(packageId);
            blindboxPackage.setStatus(PackageStatus.SOLD_OUT);
            return packageId;
        }

        throw new IllegalStateException("No available product to assign for " + productType);
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
