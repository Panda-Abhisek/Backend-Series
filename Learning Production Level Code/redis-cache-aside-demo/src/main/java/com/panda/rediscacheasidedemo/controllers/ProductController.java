package com.panda.rediscacheasidedemo.controllers;

import com.panda.rediscacheasidedemo.entities.Product;
import com.panda.rediscacheasidedemo.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    @GetMapping
    public String hello() {
        return "Hello";
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return service.getProduct(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@RequestParam Long id, @Valid @RequestBody Product product) {
        return service.updateProduct(id, product);
    }
}