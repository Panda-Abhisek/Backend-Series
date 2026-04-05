package com.panda.rediscacheasidedemo.services;

import com.panda.rediscacheasidedemo.entities.Product;
import com.panda.rediscacheasidedemo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * Fetch product using Cache-Aside strategy with Cache Stampede Protection.
     *
     * Flow:
     * 1. Check cache first (fast path).
     * 2. If cache miss, try acquiring a Redis distributed lock (SETNX).
     * 3. Only ONE request rebuilds the cache by querying the database.
     * 4. Other requests wait briefly and retry reading the cache.
     * 5. Cache entry uses randomized TTL to prevent mass expiration.
     */
    public Product getProduct(Long id) {

        // Cache key for the product
        String cacheKey = "product:" + id;

        // Lock key to protect cache rebuild
        String lockKey = "lock:product:" + id;

    /* -----------------------------------------------------------
       STEP 1: Check cache (fast path)
       ----------------------------------------------------------- */
        Product cached = (Product) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            log.info("Cache HIT for product id {}", id);
            return cached;
        }

        log.info("Cache MISS for product id {}", id);

    /* -----------------------------------------------------------
       STEP 2: Attempt to acquire distributed lock
       SETNX ensures only one thread/process gets the lock
       Lock has TTL to prevent deadlocks if service crashes
       ----------------------------------------------------------- */
        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofSeconds(10));

        if (Boolean.TRUE.equals(lockAcquired)) {

            log.info("Lock acquired for product {}", id);

            try {

            /* ---------------------------------------------------
               STEP 3: Fetch from database (slow operation)
               Only ONE request should reach here
               --------------------------------------------------- */
                Product product = productRepository
                        .findById(id)
                        .orElseThrow();

            /* ---------------------------------------------------
               STEP 4: Store result in cache
               TTL is randomized to prevent cache avalanche
               --------------------------------------------------- */
                long baseTtl = 600; // 10 minutes
                long randomExtra = ThreadLocalRandom.current().nextInt(120);
                long finalTtl = baseTtl + randomExtra;

                redisTemplate.opsForValue()
                        .set(cacheKey, product, Duration.ofSeconds(finalTtl));

                log.info("Cache populated for product {}", id);

                return product;

            } finally {

            /* ---------------------------------------------------
               STEP 5: Release lock
               --------------------------------------------------- */
                redisTemplate.delete(lockKey);
            }

        } else {

        /* -------------------------------------------------------
           STEP 6: Another request is rebuilding the cache
           Wait briefly and retry cache lookup
           ------------------------------------------------------- */
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            for (int i = 0; i < 5; i++) {

                Product retry = (Product) redisTemplate.opsForValue().get(cacheKey);

                if (retry != null) {
                    log.info("Cache filled by another thread for {}", id);
                    return retry;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }

        /* -------------------------------------------------------
           If cache is still empty, fallback to DB
           (rare but prevents request failure)
           ------------------------------------------------------- */
            return productRepository.findById(id).orElseThrow();
        }
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
