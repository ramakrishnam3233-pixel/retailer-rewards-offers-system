package com.retailer.offer.rewards.model;

import java.time.LocalDate;
import java.util.List;

public record RewardSummary(LocalDate startDate, LocalDate endDate, List<CustomerReward> customers) {}
