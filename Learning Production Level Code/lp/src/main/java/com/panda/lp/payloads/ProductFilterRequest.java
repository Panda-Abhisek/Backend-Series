package com.panda.lp.payloads;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductFilterRequest(
        String title,
        Boolean live,
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice
) {
}
