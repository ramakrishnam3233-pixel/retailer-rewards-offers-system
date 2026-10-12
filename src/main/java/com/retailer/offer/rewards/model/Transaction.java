package com.retailer.offer.rewards.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public record Transaction(
        @NotBlank String customerId,
        @NotNull LocalDate date,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount
) {}
