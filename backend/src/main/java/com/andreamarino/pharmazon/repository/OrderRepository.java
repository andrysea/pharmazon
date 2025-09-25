package com.andreamarino.pharmazon.repository;

import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    
    @Query("SELECT o FROM Order o WHERE o.state.id = :idState")
    List<Order> findWaitingOrders(@Param("idState") Long idState);

    @Query("SELECT o FROM Order o WHERE o.client.username = :username")
    List<Order> findAllByClientUsername(@Param("username") String username);

    @Query("SELECT o FROM Order o WHERE o.state.id  = :idStateOne OR o.state.id = :idStateTwo")
    List<Order> findAllWithoutStates(@Param("idStateOne") Long idStateOne, @Param("idStateTwo") Long idStateTwo);

    @Query("SELECT DISTINCT o.client " + 
                "FROM Order o " + 
                "JOIN o.cart c " + 
                "JOIN c.cartItems ci " + 
                "JOIN ci.product p " + 
                "WHERE o.state.state = :stateDelivered " + 
                "AND ci.code = :productCode " +
                "AND ci.name = :productName "+
                "AND p.code = :productCode " +
                "AND p.name = :productName "+
                "AND p.activeProduct = true")
    List<Client> findAllClientWhoHaveBuyADeliveredProduct(@Param("stateDelivered") String stateDelivered, @Param("productCode") String productCode, @Param("productName") String productName);

    Optional<Order> findByCode(String code);
    boolean existsByClientUsernameAndStateId(String username, Long stateId);
}
