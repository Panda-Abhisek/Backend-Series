package com.panda.lp.controllers;

import com.panda.lp.payloads.PageResponse;
import com.panda.lp.payloads.ProductFilterRequest;
import com.panda.lp.payloads.ProductRequest;
import com.panda.lp.payloads.ProductResponse;
import com.panda.lp.responses.ApiResponse;
import com.panda.lp.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/offset-senior")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProductsV2(
            @Valid ProductFilterRequest filter, // filter dto
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable // pageable default - Pageable pageable
    ) {
        PageResponse<ProductResponse> productsV2 = productService.getProductsV2(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", productsV2));
    }

    @GetMapping("/offset-junior")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,

            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean live,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        if (size > 50) size = 50; // cap page size to prevent abuse

        // whitelist allowed sort fields
        List<String> allowedSortFields = List.of("price", "createdAt", "title");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                sortDir.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending()
        );

        PageResponse<ProductResponse> products = productService.getProducts(
                pageable, title, live, minPrice, maxPrice
        );

        return ResponseEntity.ok(
                ApiResponse.success("Products fetched successfully", products)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse product = productService.createProduct(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created("Product created successfully", product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id
    ) {

        ProductResponse product = productService.getProductById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product fetched successfully", product)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse product = productService.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", product)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully", null)
        );
    }
}