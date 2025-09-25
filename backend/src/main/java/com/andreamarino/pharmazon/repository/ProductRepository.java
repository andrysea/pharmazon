package com.andreamarino.pharmazon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
 
    Optional<Product> findByCode(String code);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE CONCAT('%',:name,'%')")
    List<Product> findByName(@Param("name")String name);
    List<Product> findAll();

    @Query("SELECT p FROM Product p " +
           "JOIN Category c ON p.category.id = c.id " +
           "WHERE c.name = :name")
    List<Product> findByCategory(@Param("name")String name);
    
    @Query("SELECT p FROM Product p " +
    "JOIN Category c ON p.category.id = c.id " +
    "WHERE c.name = :name AND p.quantity > 0 AND p.activeProduct = true")
    List<Product> findByCategoryCheckQuantity(@Param("name")String name);

    @Query("SELECT p FROM Product p " +
           "WHERE p.quantity > 0 AND p.activeProduct = true")
    List<Product> findByActiveProductAndQuantity();
}
