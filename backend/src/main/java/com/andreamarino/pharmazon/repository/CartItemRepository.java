package com.andreamarino.pharmazon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.CartItem;
import jakarta.transaction.Transactional;

@Repository
public interface  CartItemRepository extends JpaRepository<CartItem, Long>{

    @Query("SELECT ci FROM CartItem ci " +
       "JOIN ci.cart c " +
       "JOIN ci.product p " +
       "WHERE c.id = :cartId " +
       "AND p.id = :productId " +
       "AND p.activeProduct = true " +
       "AND ci.flag = false")
    Optional <CartItem> existCartItemInCart(@Param("productId") Long productId, @Param("cartId") Long cartId);

    @Query("SELECT ci FROM CartItem ci " +
    "JOIN ci.cart c " +
    "JOIN c.client u " +
    "JOIN ci.product p " +
    "WHERE c.id = :cartId " +
    "AND u.id = :userId " + 
    "AND p.activeProduct = true " + 
    "AND ci.flag = false")
    List<CartItem> findCartItemsInCart(@Param("cartId") Long cartId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.id = :cartItemId")
    void deleteCartItemFromCart(@Param("cartId") Long cartId, @Param("cartItemId") Long cartItemId);

    @Query("SELECT ci FROM CartItem ci " +
       "JOIN ci.cart c " +
       "JOIN c.orders o " +
       "WHERE o.state.id = :idState " +
       "AND o.client.username = :username ")
    List<CartItem> findCartItemsInDeliveredOrderForClient(@Param("idState") Long idState, @Param("username") String username);

    @Query("SELECT ci FROM CartItem ci " +
    "JOIN ci.cart c " +
    "JOIN c.orders o " +
    "WHERE o.client.username = :username " +
    "AND ci.name = :name " +
    "AND ci.code = :code ")
    List<CartItem> findByCodeAndNameAndUsername(@Param("code") String code, @Param("name") String name, @Param("username") String username);
}
