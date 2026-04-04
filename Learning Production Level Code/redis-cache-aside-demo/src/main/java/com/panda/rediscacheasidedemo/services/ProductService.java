package com.panda.rediscacheasidedemo.services;

import com.panda.rediscacheasidedemo.entities.Product;
import com.panda.rediscacheasidedemo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public Product getProduct(Long id) {
        // key generation
        String key =  "product:" + id;

        // cache check
        Product cached = (Product) redisTemplate.opsForValue().get(key); // <- this is cache query

        if(cached != null) {
            log.info("Cache hit for product id: {}", id);
            return cached; // cache hit
        }

        log.info("Cache miss for product id: {}", id);
        Product product = productRepository.findById(id).orElseThrow(); // <- this is db query

        redisTemplate.opsForValue().set(key, product, 10, java.util.concurrent.TimeUnit.MINUTES); // cache set with expiration

        return product;
    }

    public Product updateProduct(Long id, Product req) {

        Product product = productRepository.findById(id).orElseThrow();

        product.setName(req.getName());
        product.setPrice(req.getPrice());

        productRepository.save(product);

        redisTemplate.delete("product:" + id);

        return product;
    }
}
