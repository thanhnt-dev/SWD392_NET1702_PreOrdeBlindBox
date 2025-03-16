package com.swd392.preOrderBlindBox.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
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
}
