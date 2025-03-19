package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.common.enums.*;
import com.swd392.preOrderBlindBox.common.exception.ResourceNotFoundException;
import com.swd392.preOrderBlindBox.common.util.Util;
import com.swd392.preOrderBlindBox.entity.*;
import com.swd392.preOrderBlindBox.repository.repository.PreorderItemRepository;
import com.swd392.preOrderBlindBox.repository.repository.PreorderRepository;
import com.swd392.preOrderBlindBox.restcontroller.response.*;
import com.swd392.preOrderBlindBox.service.service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ModelMapper modelMapper;
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

    @Override
    public List<PreordersHistoryResponse> getPreordersOfUser() {
        User user = userService.getCurrentUser()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        return preorderRepository.findByUserId(user.getId()).stream()
                .map(this::toPreordersHistoryResponse)
                .collect(Collectors.toList());
    }

    private PreordersHistoryResponse toPreordersHistoryResponse(Preorder preorder) {
        PreordersHistoryResponse response = modelMapper.map(preorder, PreordersHistoryResponse.class);
        return response;
    }

    @Override
    public PreorderDetailsResponse getPreorderDetails(Long preorderId) {
        Preorder preorder = preorderRepository.findById(preorderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        PreorderDetailsResponse response = modelMapper.map(preorder, PreorderDetailsResponse.class);
        response.setUsername(preorder.getUser().getEmail());
        response.setPayments(buildPaymentSummaries(preorder));
        response.setItems(buildPreorderItems(preorder.getPreorderItems()));
        response.setCreatedAt(Util.convertTimestampToLocalDateTime(preorder.getCreatedAt()));

        return response;
    }

    @Override
    public List<PreorderItem> getPreorderItemsAssociatedWithBlindboxSeries(Long seriesId) {
        return preorderItemRepository.findByBlindboxSeriesId(seriesId);
    }

    @Override
    public void updatePreorderItem(PreorderItem preorderItem) {
        preorderItemRepository.save(preorderItem);
    }

    @Override
    public void updatePreorderPrice(Long preorderId) {
        Preorder preorder = preorderRepository.findById(preorderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
        boolean isAllLocked = preorder.getPreorderItems().stream()
                .allMatch(item -> item.getLockedPrice() != null);

        updatePreorderEstimatedTotalAmount(preorderId);
        if (isAllLocked) {
            updatePreorderTotalAmount(preorderId);
        }

        preorderRepository.save(preorder);
    }

    @Override
    public void updatePreorderTotalAmount(Long preorderId) {
        Preorder preorder = preorderRepository.findById(preorderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));
        for (PreorderItem item : preorder.getPreorderItems()) {
            if (item.getLockedPrice() == null) {
                throw new IllegalStateException("Cannot update total amount for preorder with item's locked price not set");
            }
        }

        BigDecimal totalAmount = preorder.getPreorderItems().stream()
                .map(PreorderItem::getLockedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        preorder.setTotalAmount(totalAmount);
        preorder.setRemainingAmount(Util.normalizePrice(totalAmount.subtract(preorder.getDepositAmount())));
        preorderRepository.save(preorder);
    }

    @Override
    public void updatePreorderEstimatedTotalAmount(Long preorderId) {
        Preorder preorder = preorderRepository.findById(preorderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCES_NOT_FOUND));

        BigDecimal estimatedTotalAmount = preorder.getPreorderItems().stream()
                .map(PreorderItem::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        preorder.setEstimatedTotalAmount(estimatedTotalAmount);
        preorderRepository.save(preorder);
    }

    private List<PaymentSummaryResponse> buildPaymentSummaries(Preorder preorder) {
        return preorder.getTransactions().stream()
                .map(this::toPaymentSummaryResponse)
                .collect(Collectors.toList());
    }

    private PaymentSummaryResponse toPaymentSummaryResponse(Transaction transaction) {
        PaymentSummaryResponse response = new PaymentSummaryResponse();
        response.setAmount(transaction.getTransactionAmount());
        response.setDeposit(transaction.getIsDeposit());
        response.setIssuedAt(Util.convertTimestampToLocalDateTime(transaction.getCreatedAt()));
        response.setStatus(transaction.getTransactionStatus());
        return response;
    }

    private List<PreorderItemResponse> buildPreorderItems(List<PreorderItem> preorderItems) {
        return preorderItems.stream()
                .map(this::toPreorderItemResponse)
                .collect(Collectors.toList());
    }

    private PreorderItemResponse toPreorderItemResponse(PreorderItem preorderItem) {
        PreorderItemResponse response = modelMapper.map(preorderItem, PreorderItemResponse.class);
        response.setProducts(buildProductResponses(preorderItem));
        return response;
    }

    private List<PreorderItemProductResponse> buildProductResponses(PreorderItem preorderItem) {
        List<Long> productIds = parseProductIds(preorderItem.getProductIds());
        return productIds.stream()
                .map(productId -> toPreorderItemProductResponse(productId, preorderItem))
                .collect(Collectors.toList());
    }

    private PreorderItemProductResponse toPreorderItemProductResponse(Long productId, PreorderItem preorderItem) {
        PreorderItemProductResponse response = new PreorderItemProductResponse();
        response.setId(productId);
        response.setSeriesId(preorderItem.getBlindboxSeries().getId());
        response.setAlias(generateProductAlias(productId, preorderItem));
        return response;
    }

    private List<Long> parseProductIds(String productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return Arrays.stream(productIds.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid product_ids format: " + productIds, e);
        }
    }

    private String generateProductAlias(Long productId, PreorderItem preorderItem) {
        ProductType type = preorderItem.getProductType();
        if (type == ProductType.BOX) {
            Blindbox blindbox = blindboxService.getBlindboxById(productId);
            return "Blindbox #" + productId + " of Package #" + blindbox.getBlindboxPackage().getId();
        } else if (type == ProductType.PACKAGE) {
            return "Package #" + productId;
        }
        return null;
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
