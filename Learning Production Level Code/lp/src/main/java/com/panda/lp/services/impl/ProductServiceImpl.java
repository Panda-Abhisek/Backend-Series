package com.panda.lp.services.impl;

import com.panda.lp.config.ProductMapper;
import com.panda.lp.exceptions.ProductNotFoundException;
import com.panda.lp.models.Product;
import com.panda.lp.payloads.PageResponse;
import com.panda.lp.payloads.ProductFilterRequest;
import com.panda.lp.payloads.ProductRequest;
import com.panda.lp.payloads.ProductResponse;
import com.panda.lp.product.specifications.ProductSpecification;
import com.panda.lp.repositories.ProductRepository;
import com.panda.lp.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = ProductMapper.toEntity(request);
        Product saved = productRepository.save(product);
        return ProductMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(productId)
        );
        return ProductMapper.toResponse(product);
    }

    @Override
    public PageResponse<ProductResponse> getProductsV2(ProductFilterRequest filters, Pageable pageable) {
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

        Specification<Product> productSpecification = ProductSpecification.withFilters(filters);
        Page<ProductResponse> all = productRepository.findAll(productSpecification, pageable)
                .map(ProductMapper::toResponse);
        return new PageResponse<>(
                all.getContent(),
                all.getNumber(),
                all.getSize(),
                all.getTotalElements(),
                all.getTotalPages(),
                all.isLast(),
                all.isFirst()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProductResponse> getProducts(Pageable pageable, String title, Boolean live, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Product> specification = Specification
                .where(ProductSpecification.hasTitleLike(title))
                .and(ProductSpecification.isLive(live))
                .and(ProductSpecification.minPrice(minPrice))
                .and(ProductSpecification.maxPrice(maxPrice));

        Page<ProductResponse> page = productRepository.findAll(specification, pageable)
                .map(ProductMapper::toResponse);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst()
        );
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(productId)
        );
        // Update fields
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setPrice(request.getPrice());
        product.setLive(request.isLive());

        Product updated = productRepository.save(product);
        return ProductMapper.toResponse(updated);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
        productRepository.deleteById(productId);
    }
}
