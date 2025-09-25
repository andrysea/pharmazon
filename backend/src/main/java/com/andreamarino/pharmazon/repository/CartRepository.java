package com.andreamarino.pharmazon.repository;

import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.andreamarino.pharmazon.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{

    @Query("SELECT c From Cart c WHERE c.client.username = :username AND c.activeCart = true")
    Optional <Cart> getCartOfClient(@Param("username") String username);
}
