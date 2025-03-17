package com.swd392.preOrderBlindBox.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static List<Long> getProductIdsAsList(String productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return Arrays.stream(productIds.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid product_ids format: " + productIds, e);
        }
    }

    public static BigDecimal calculatePriceWithCoefficient(BigDecimal price, BigDecimal coefficient) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0 ||
                coefficient == null || coefficient.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price and coefficient must be non-null and non-negative");
        }
        return price.multiply(coefficient).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal normalizePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-null and non-negative");
        }
        return price.setScale(SCALE, ROUNDING_MODE);
    }
}
