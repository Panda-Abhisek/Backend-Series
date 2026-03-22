package com.panda.lp.services;

import com.panda.lp.payloads.PageResponse;
import com.panda.lp.payloads.ProductFilterRequest;
import com.panda.lp.payloads.ProductRequest;
import com.panda.lp.payloads.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    void deleteProduct(Long productId);

    PageResponse<ProductResponse> getProducts(Pageable pageable, String title, Boolean live, BigDecimal minPrice, BigDecimal maxPrice);

    PageResponse<ProductResponse> getProductsV2(@Valid ProductFilterRequest filter, Pageable pageable);
}
