package com.retailer.offer.rewards.service;

import com.retailer.offer.rewards.exception.InvalidDateRangeException;
import com.retailer.offer.rewards.model.*;
import com.retailer.offer.rewards.model.CustomerReward;
import com.retailer.offer.rewards.model.MonthlyReward;
import com.retailer.offer.rewards.model.RewardSummary;
import com.retailer.offer.rewards.model.Transaction;
import com.retailer.offer.rewards.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RewardService {
    private static final BigDecimal FIFTY = new BigDecimal("50");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private final TransactionRepository repository;

    public RewardService(TransactionRepository repository) { this.repository = repository; }

    public long calculatePoints(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative or null");
        }
        // Only whole dollars earn points; cents are ignored by flooring each tier.
        long points = 0;
        if (amount.compareTo(ONE_HUNDRED) > 0) {
            points += amount.subtract(ONE_HUNDRED).setScale(0, java.math.RoundingMode.DOWN).longValueExact() * 2;
            points += 50; // all dollars in the $50-$100 tier
        } else if (amount.compareTo(FIFTY) > 0) {
            points += amount.subtract(FIFTY).setScale(0, java.math.RoundingMode.DOWN).longValueExact();
        }
        return points;
    }

    public RewardSummary getRewards(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDateRangeException("startDate and endDate are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("startDate must be on or before endDate");
        }

        Map<String, Map<YearMonth, Long>> pointsByCustomer = new TreeMap<>();
        for (Transaction transaction : repository.findBetween(startDate, endDate)) {
            long points = calculatePoints(transaction.amount());
            YearMonth month = YearMonth.from(transaction.date());
            pointsByCustomer.computeIfAbsent(transaction.customerId(), ignored -> new TreeMap<>())
                    .merge(month, points, Long::sum);
        }

        List<CustomerReward> customers = pointsByCustomer.entrySet().stream().map(entry -> {
            List<MonthlyReward> monthly = entry.getValue().entrySet().stream()
                    .map(e -> new MonthlyReward(e.getKey().format(MONTH_FORMAT), e.getValue())).toList();
            long total = entry.getValue().values().stream().mapToLong(Long::longValue).sum();
            return new CustomerReward(entry.getKey(), monthly, total);
        }).toList();
        return new RewardSummary(startDate, endDate, customers);
    }
}
