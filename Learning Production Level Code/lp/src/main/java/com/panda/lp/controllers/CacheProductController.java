package com.panda.lp.controllers;

import com.panda.lp.payloads.PaginatedResponse;
import com.panda.lp.payloads.ProductFilterRequest;
import com.panda.lp.payloads.ProductResponse;
import com.panda.lp.responses.ApiResponse;
import com.panda.lp.services.ProductCachedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache/products")
@RequiredArgsConstructor
public class CacheProductController {
    private final ProductCachedService productCachedService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getAllProducts(
            @Valid ProductFilterRequest filterRequest,
            @PageableDefault(value = 10, size = 10, page = 0, sort = "price", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PaginatedResponse<ProductResponse> response = productCachedService.getAllProductsPaginatedWithCachingEnabled(filterRequest, pageable);
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully from /cache/products.", response));
    }
}
