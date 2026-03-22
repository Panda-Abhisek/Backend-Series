package com.panda.lp.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 500, message = "Short description too long, max 500 characters")
    private String shortDescription;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive")
    private BigDecimal price;

    private boolean live;
}
