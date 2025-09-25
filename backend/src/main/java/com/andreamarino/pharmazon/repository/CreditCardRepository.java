package com.andreamarino.pharmazon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.CreditCard;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>{
    @Query("SELECT c FROM CreditCard c JOIN c.client u WHERE u.username = :username AND c.active = TRUE")
    List<CreditCard> findByUsername(@Param("username") String username);

    Optional<CreditCard> findByNumber(String number);
}
