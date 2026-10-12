package com.retailer.offer.rewards.repository;

import com.retailer.offer.rewards.model.Transaction;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    private final List<Transaction> transactions = new CopyOnWriteArrayList<>();

    public InMemoryTransactionRepository() {
        // Sample transactions spanning multiple months; month names are never hard-coded.
        transactions.add(new Transaction("CUST00", LocalDate.parse("2026-01-10"), new java.math.BigDecimal("120.00")));
        transactions.add(new Transaction("CUST01", LocalDate.parse("2026-01-15"), new java.math.BigDecimal("80.00")));
        transactions.add(new Transaction("CUST02", LocalDate.parse("2026-01-30"), new java.math.BigDecimal("300.00")));
        transactions.add(new Transaction("CUST03", LocalDate.parse("2026-02-01"), new java.math.BigDecimal("40.00")));
        transactions.add(new Transaction("CUST04", LocalDate.parse("2026-02-05"), new java.math.BigDecimal("60.00")));
        transactions.add(new Transaction("CUST05", LocalDate.parse("2026-02-10"), new java.math.BigDecimal("49.99")));
        transactions.add(new Transaction("CUST06", LocalDate.parse("2026-03-01"), new java.math.BigDecimal("75.00")));
        transactions.add(new Transaction("CUST07", LocalDate.parse("2026-03-05"), new java.math.BigDecimal("99.50")));
        transactions.add(new Transaction("CUST08", LocalDate.parse("2026-03-10"), new java.math.BigDecimal("70.00")));
    }

    @Override public List<Transaction> findAll() { return List.copyOf(transactions); }

    @Override public void save(Transaction transaction) { transactions.add(transaction); }

    @Override public List<Transaction> findBetween(LocalDate start, LocalDate end) {
        return transactions.stream().filter(t -> !t.date().isBefore(start) && !t.date().isAfter(end)).toList();
    }
}
