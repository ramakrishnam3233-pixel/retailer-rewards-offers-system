package com.retailer.offer.rewards.service;

import com.retailer.offer.rewards.exception.InvalidDateRangeException;
import com.retailer.offer.rewards.model.Transaction;
import com.retailer.offer.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RewardServiceTest {
    private InMemoryTestRepository repository;
    private RewardService service;

    @BeforeEach void setUp() {
        repository = new InMemoryTestRepository();
        service = new RewardService(repository);
    }

    @Test void calculatesTierBoundaries() {
        assertEquals(0, service.calculatePoints(new BigDecimal("0")));
        assertEquals(0, service.calculatePoints(new BigDecimal("50")));
        assertEquals(1, service.calculatePoints(new BigDecimal("51")));
        assertEquals(50, service.calculatePoints(new BigDecimal("100")));
        assertEquals(90, service.calculatePoints(new BigDecimal("120")));
        assertEquals(90, service.calculatePoints(new BigDecimal("120.99")));
        assertEquals(90, service.calculatePoints(new BigDecimal("120.00")));
    }

    @Test void aggregatesMultipleTransactionsByCustomerAndMonth() {
        repository.items.add(new Transaction("A", LocalDate.parse("2025-01-01"), new BigDecimal("120")));
        repository.items.add(new Transaction("A", LocalDate.parse("2025-01-02"), new BigDecimal("75")));
        repository.items.add(new Transaction("A", LocalDate.parse("2025-02-02"), new BigDecimal("200")));
        var result = service.getRewards(LocalDate.parse("2025-01-01"), LocalDate.parse("2025-03-31"));
        assertEquals(1, result.customers().size());
        assertEquals(115, result.customers().get(0).monthlyRewards().get(0).points());
        assertEquals(365, result.customers().get(0).totalPoints());
    }

    @Test void rejectsStartDateAfterEndDate() {
        assertThrows(InvalidDateRangeException.class, () ->
                service.getRewards(LocalDate.parse("2025-03-01"), LocalDate.parse("2025-01-01")));
    }

    @Test void rejectsNegativeAmountForDirectCalculation() {
        assertThrows(IllegalArgumentException.class, () -> service.calculatePoints(new BigDecimal("-1")));
    }

    static class InMemoryTestRepository implements TransactionRepository {
        final List<Transaction> items = new ArrayList<>();
        public List<Transaction> findAll() { return List.copyOf(items); }
        public void save(Transaction t) { items.add(t); }
        public List<Transaction> findBetween(LocalDate start, LocalDate end) {
            return items.stream().filter(t -> !t.date().isBefore(start) && !t.date().isAfter(end)).toList();
        }
    }
}
