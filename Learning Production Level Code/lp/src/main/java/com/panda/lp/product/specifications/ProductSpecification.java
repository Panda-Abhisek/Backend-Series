package com.panda.lp.product.specifications;

import com.panda.lp.models.Product;
import com.panda.lp.payloads.ProductFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> hasTitleLike(String title) {
        return ((root, query, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
    }

    public static Specification<Product> isLive(Boolean live) {
        return ((root, query, cb) ->
                live == null ? null : cb.equal(root.get("live"), live));
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return ((root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice));
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return ((root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice));
    }

    // v2 starts here
    public static Specification<Product> withFilters(ProductFilterRequest filters) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filters.title() != null && !filters.title().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + filters.title().toLowerCase() + "%"
                        )
                );
            }

            if (filters.live() != null) {
                predicates.add(cb.equal(root.get("live"), filters.live()));
            }

            if (filters.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filters.minPrice()));
            }

            if (filters.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filters.maxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // another way of building specification
    public static Specification<Product> build(String search, Boolean live, BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + search.toLowerCase() + "%"
                        )
                );
            }

            if (live != null) {
                predicates.add(cb.equal(root.get("live"), live));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
