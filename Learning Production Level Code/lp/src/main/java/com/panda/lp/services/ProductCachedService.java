package com.panda.lp.services;

import com.panda.lp.payloads.PaginatedResponse;
import com.panda.lp.payloads.ProductFilterRequest;
import com.panda.lp.payloads.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface ProductCachedService {
    PaginatedResponse<ProductResponse> getAllProductsPaginatedWithCachingEnabled(@Valid ProductFilterRequest filterRequest, Pageable pageable);
}
