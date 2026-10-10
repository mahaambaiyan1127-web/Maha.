package com.maha.shoppingmart.repository;

import com.maha.shoppingmart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}