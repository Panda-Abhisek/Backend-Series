package com.panda.lp.config;

import com.panda.lp.payloads.ProductRequest;
import com.panda.lp.payloads.ProductResponse;
import com.panda.lp.models.Product;

public class ProductMapper {

    public static Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setPrice(request.getPrice());
        product.setLive(request.isLive());
        return product;
    }

    public static ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        response.setShortDescription(product.getShortDescription());
        response.setPrice(product.getPrice());
        response.setLive(product.isLive());
        response.setOutOfStock(product.isOutOfStock());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}