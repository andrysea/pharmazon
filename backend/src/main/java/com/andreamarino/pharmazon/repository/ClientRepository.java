package com.andreamarino.pharmazon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.andreamarino.pharmazon.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    
}
