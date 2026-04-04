package com.panda.rediscacheasidedemo.repositories;

import com.panda.rediscacheasidedemo.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
