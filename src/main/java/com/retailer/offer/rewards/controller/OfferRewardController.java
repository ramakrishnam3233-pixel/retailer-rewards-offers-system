package com.retailer.offer.rewards.controller;

import com.retailer.offer.rewards.model.RewardSummary;
import com.retailer.offer.rewards.model.Transaction;
import com.retailer.offer.rewards.repository.TransactionRepository;
import com.retailer.offer.rewards.service.RewardService;
import com.retailer.offer.rewards.util.AppConstants;
import com.retailer.offer.rewards.util.URIConstants;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping(URIConstants.API_BASE_ENDPOINT)
public class OfferRewardController {
    private final RewardService rewardService;
    private final TransactionRepository repository;

    public OfferRewardController(RewardService rewardService, TransactionRepository repository) {
        this.rewardService = rewardService;
        this.repository = repository;
    }

    @GetMapping(URIConstants.REWARDS_ENDPOINT)
    public RewardSummary getRewards(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return rewardService.getRewards(startDate, endDate);
    }

    @PostMapping(URIConstants.TRANSACTIONS_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> addTransaction(@Valid @RequestBody Transaction transaction) {
        repository.save(transaction);
        return Map.of(AppConstants.MESSAGE, AppConstants.TRANSACTION_SAVED_SUCCESSFULLY);
    }
}
