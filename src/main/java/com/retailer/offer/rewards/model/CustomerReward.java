package com.retailer.offer.rewards.model;

import java.util.List;

public record CustomerReward(String customerId, List<MonthlyReward> monthlyRewards, long totalPoints) {}
