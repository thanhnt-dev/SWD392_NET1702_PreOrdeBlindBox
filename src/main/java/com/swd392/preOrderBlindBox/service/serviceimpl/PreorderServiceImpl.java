package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.*;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.repository.repository.PreorderItemRepository;
import com.swd392.preOrderBlindBox.repository.repository.PreorderRepository;
import com.swd392.preOrderBlindBox.service.service.*;
import lombok.RequiredArgsConstructor;
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

    @Override
    public Preorder createPreorder(Preorder preorderRequest) {
        Cart cart = cartService.getOrCreateCart();
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        preorderRequest.setUser(userService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND)));
        preorderRequest.setOrderCode(generateOrderCode());
        preorderRequest.setTotalPrice(cartService.calculateCartTotal());
        preorderRequest.setPreorderStatus(PreorderStatus.DEPOSIT_PENDING);

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

    private PreorderItem getPreorderItem(Preorder preorderRequest, CartItem cartItem) {
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
                .quantity(cartItem.getQuantity())
                .productType(cartItem.getProductType());

        if (cartItem.getItemCampaignType() == CampaignType.MILESTONE || cartItem.getItemCampaignType() == null) {
            builder.lockedPrice(itemTotal);
        }

        return builder.build();
    }

    @Override
    public Optional<Preorder> getPreorderById(Long id) {
        return preorderRepository.findById(id);
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
        Preorder preorder = preorderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        preorder.setPreorderStatus(status);
        return preorderRepository.save(preorder);
    }

    @Override
    public BigDecimal calculateDepositAmount(Long preorderId) {
        return null;
    }

    @Override
    public BigDecimal calculateFullPaymentAmount(Long preorderId) {
        List<PreorderItem> preorderItems = preorderItemRepository.findByPreorderId(preorderId);

        BigDecimal totalAmount = preorderItems.stream()
                .map(preorderItem -> preorderItem.getItemFromCampaignType() == CampaignType.MILESTONE ||
                        preorderItem.getItemFromCampaignType() == null ?
                        preorderItem.getLockedPrice() :
                        preorderItem.getOriginalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
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
