package com.swd392.preOrderBlindBox.enums;

/**
 * Represents the type of campaign:
 * MILESTONE - For tiered discounts on in-stock products (Flow 1)
 * GROUP - For pre-order campaigns where the product isn't in stock yet (Flow 2)
 */
public enum CampaignType {
  // For tiers based on quantity milestones, with in-stock products
  MILESTONE,

  // For group pre-order campaigns where product is not yet in stock
  GROUP;
}