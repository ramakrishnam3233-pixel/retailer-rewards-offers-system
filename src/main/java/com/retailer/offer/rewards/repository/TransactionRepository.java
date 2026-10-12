package com.retailer.offer.rewards.repository;

import com.retailer.offer.rewards.model.Transaction;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> findAll();
    void save(Transaction transaction);
    List<Transaction> findBetween(LocalDate start, LocalDate end);
}
