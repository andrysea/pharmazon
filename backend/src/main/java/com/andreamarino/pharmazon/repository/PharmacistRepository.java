package com.andreamarino.pharmazon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.andreamarino.pharmazon.model.Pharmacist;

public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {
    
}
