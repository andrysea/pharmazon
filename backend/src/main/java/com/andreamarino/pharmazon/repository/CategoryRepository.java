package com.andreamarino.pharmazon.repository;

import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
    Optional<Category> findByName(String name);
    Optional<Category> findByCode(String code);
}
