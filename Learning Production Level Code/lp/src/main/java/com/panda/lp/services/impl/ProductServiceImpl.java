package com.panda.lp.services.impl;

import com.panda.lp.config.ProductMapper;
import com.panda.lp.exceptions.ProductNotFoundException;
import com.panda.lp.models.Product;
import com.panda.lp.payloads.*;
import com.panda.lp.product.specifications.ProductSpecification;
import com.panda.lp.repositories.ProductRepository;
import com.panda.lp.services.ProductService;
import com.panda.lp.utils.ScrollPositionCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ScrollResponse<ProductResponse> getAllWithScroll(
            String search,
            Boolean live,
            BigDecimal minPrice,
            BigDecimal maxPrice,

            String scrollId,
            int size,
            String sortBy,
            String sortDir
    ) {
        // Decode scroll position
        ScrollPosition position = ScrollPositionCodec.decode(scrollId);

        // Build filter specification - if we want then we can create specification right here manually like we did in gerProducts() - BUT here I'm doing differently
        Specification<Product> build = ProductSpecification.build(search, live, minPrice, maxPrice);

        // Build dynamic sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Build dynamic sort by field
        String validatedSorty =
                switch (sortBy.toLowerCase()) {
                    case "productid", "id" -> "productId";
                    case "title", "name" -> "title";
                    case "price" -> "price";
                    case "createat", "created", "createdat" -> "createdAt";
                    default -> "price"; // Default sort field
                };
        Sort sort = Sort.by(direction, validatedSorty);

        // Fetch products using Window API with fluent query
        Window<Product> window = productRepository.findBy(build,
                q -> q.limit(size).sortBy(sort).scroll(position));

        // Convert to DTOs - here I'm using BeanUtils || we also can use ModelMapper or MapStruct
        List<ProductResponse> list = window.getContent().stream()
                .map(content -> {
                    ProductResponse response = new ProductResponse();
                    BeanUtils.copyProperties(content, response);
                    return response;
                })
                .toList();

        // Get next scroll position (only if there are items and more pages exist)
        String nextScrollId = null;
        if(!window.isEmpty() && window.hasNext()) {
            ScrollPosition nextPosition = window.positionAt(window.size() - 1);
            nextScrollId = ScrollPositionCodec.encode(nextPosition);
        }

        // Build response
        ScrollResponse<ProductResponse> res = new ScrollResponse<>();
        res.setItems(list);
        res.setHasNext(window.hasNext());
        res.setScrollId(nextScrollId);
        res.setPageSize(size);

        return res;
    }

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
    public PaginatedResponse<ProductResponse> getProductsV2(ProductFilterRequest filters, Pageable pageable) {
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
        return new PaginatedResponse<>(
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
    public PaginatedResponse<ProductResponse> getProducts(Pageable pageable, String title, Boolean live, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Product> specification = Specification
                .where(ProductSpecification.hasTitleLike(title))
                .and(ProductSpecification.isLive(live))
                .and(ProductSpecification.minPrice(minPrice))
                .and(ProductSpecification.maxPrice(maxPrice));

        Page<ProductResponse> page = productRepository.findAll(specification, pageable)
                .map(ProductMapper::toResponse);
        return new PaginatedResponse<>(
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
