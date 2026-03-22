package com.panda.lp.payloads;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponse {

    private Long productId;
    private String title;
    private String shortDescription;
    private String description;
    private BigDecimal price;
    private boolean live;
    private boolean outOfStock;
    private LocalDateTime createdAt;
}