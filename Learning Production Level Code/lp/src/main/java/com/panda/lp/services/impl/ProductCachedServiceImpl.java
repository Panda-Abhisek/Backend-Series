package com.panda.lp.services.impl;

import com.panda.lp.config.ProductMapper;
import com.panda.lp.models.Product;
import com.panda.lp.payloads.PaginatedResponse;
import com.panda.lp.payloads.ProductFilterRequest;
import com.panda.lp.payloads.ProductResponse;
import com.panda.lp.product.specifications.ProductSpecification;
import com.panda.lp.repositories.ProductRepository;
import com.panda.lp.services.ProductCachedService;
import com.panda.lp.services.ProductRedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCachedServiceImpl implements ProductCachedService {
    private final ProductRepository productRepository;
    private final ProductRedisCacheService productRedisCacheService;

    /**
     * Here we have to create specification for dynamic query - we have ProductSpecification.withFilters()
     *
     */
    @Override
    public PaginatedResponse<ProductResponse> getAllProductsPaginatedWithCachingEnabled(ProductFilterRequest filters, Pageable pageable) {

        log.debug("Checking cache for products with filters={}, pageable={}", filters, pageable);
        PaginatedResponse<ProductResponse> cachedOffsetResponse = productRedisCacheService.getCachedOffsetResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString(),
                pageable.getSort().toString(),
                filters.title(),
                filters.live(),
                filters.minPrice(),
                filters.maxPrice()
        );
        if (cachedOffsetResponse != null) {
            log.debug("Cache hit for products with filters={}, pageable={}", filters, pageable);
            return cachedOffsetResponse;
        }
        log.debug("Cache miss for products with filters={}, pageable={}", filters, pageable);

        if (filters == null) {
            return null;
        }
        if (filters.minPrice() != null && filters.maxPrice() != null
                && filters.minPrice().compareTo(filters.maxPrice()) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }

        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    50,
                    pageable.getSort()
            );
        }

        // Specificaitons
        Specification<Product> productSpecification = ProductSpecification.withFilters(filters);

        /* *
         * so here we'll be caching the data - if cache-miss then db-hits otherwise response returned from cache - i.e., cache-hit
         * dependencies required -
         * -- spring-boot-starter-data-redis
         * -- spring-boot-starter-cache
         * -- jedis from redis.clients
         * */
        // if CACHE-HIT -> then get data from Cache and return

        // CACHE-MISS -> DB hit
        Page<ProductResponse> page = productRepository.findAll(productSpecification, pageable).map(ProductMapper::toResponse);

        PaginatedResponse<ProductResponse> response = new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst()
        );

        // Cache the response for future requests
        productRedisCacheService.cacheOffsetResponse(
                response,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString(),
                pageable.getSort().toString(),
                filters.title(),
                filters.live(),
                filters.minPrice(),
                filters.maxPrice()
        );

        return response;
    }
}
