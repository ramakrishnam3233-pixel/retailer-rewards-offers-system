package com.retailer.offer.rewards;

import com.retailer.offer.rewards.model.Transaction;
import com.retailer.offer.rewards.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired TransactionRepository repository;
    @Autowired ObjectMapper objectMapper;

    @Test void returnsMonthlyAndTotalRewards() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers[0].customerId").exists())
                .andExpect(jsonPath("$.customers[0].totalPoints").exists());
    }

    @Test void rejectsReversedDateRange() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .param("startDate", "2025-03-31")
                        .param("endDate", "2025-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("startDate must be on or before endDate"));
    }

    @Test void rejectsMissingDateParameters() throws Exception {
        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isBadRequest());
    }

    @Test void createsTransactionAndRejectsNegativeAmount() throws Exception {
        var valid = new Transaction("IT-CUSTOMER", LocalDate.parse("2025-04-01"), new BigDecimal("120.00"));
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isCreated());

        var invalid = new Transaction("IT-CUSTOMER", LocalDate.parse("2025-04-01"), new BigDecimal("-10.00"));
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
