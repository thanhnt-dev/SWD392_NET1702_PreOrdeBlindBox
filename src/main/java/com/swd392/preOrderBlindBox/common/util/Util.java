package com.swd392.preOrderBlindBox.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Util {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static LocalDateTime convertTimestampToLocalDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
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
