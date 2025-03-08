package com.swd392.preOrderBlindBox.response;

import com.swd392.preOrderBlindBox.entity.Campaign;
import jakarta.persistence.*;

public class CampaignTierResponse {
    private String tierName;
    private int minQuantity;
    private int maxQuantity;
    private int discountPercent;
}
