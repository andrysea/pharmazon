package com.andreamarino.pharmazon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
    @Query("SELECT a FROM Address a WHERE a.client.username = :username AND a.active = TRUE")
    List<Address> findAllByUsername(String username);
    Optional<Address> findByCode(String code);
}
