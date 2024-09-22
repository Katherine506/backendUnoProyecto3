package com.project.demo.logic.entity.product;

import com.project.demo.logic.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long>{
}
