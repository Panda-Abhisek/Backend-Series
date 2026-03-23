package com.panda.lp.services;

import com.panda.lp.payloads.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    void deleteProduct(Long productId);

    PaginatedResponse<ProductResponse> getProducts(Pageable pageable, String title, Boolean live, BigDecimal minPrice, BigDecimal maxPrice);

    PaginatedResponse<ProductResponse> getProductsV2(@Valid ProductFilterRequest filter, Pageable pageable);

    ScrollResponse<ProductResponse> getAllWithScroll(String search, Boolean live, BigDecimal minPrice, BigDecimal maxPrice, String scrollId, int size, String sortBy, String sortDir);
}
