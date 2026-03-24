package com.panda.lp.config;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.StringJoiner;

@Component
public class RedisKeyGenerator {
    private static final String OFFSET_CACHE_PREFIX = "products=offset";

    // Create Key
    public String generateOffsetCacheKey(
            int page,
            int size,
            String sortBy,
            String sortDir,

            String title,
            Boolean live,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(OFFSET_CACHE_PREFIX);
        addIfNotNull(joiner, "page", page);
        addIfNotNull(joiner, "title", title);
        addIfNotNull(joiner, "live", live);
        addIfNotNull(joiner, "minPrice", minPrice);
        addIfNotNull(joiner, "maxPrice", maxPrice); // this will add the key-value pair to the joiner only if the value is not null
        joiner.add("size=" + size);
        joiner.add("sortBy=" + sortBy);
        joiner.add("sortDir=" + sortDir);

        return joiner.toString(); // this will return the final key as a string
    }

    public String generateOffsetCachePattern() {
        return OFFSET_CACHE_PREFIX + ":*";
    }

    private void addIfNotNull(StringJoiner joiner, String key, Object value) {
        if(value != null) {
            joiner.add(key + "=" + value); // it will just join the key and value with "+" -> key=value
        }
    }
}
