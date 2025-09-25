package com.andreamarino.pharmazon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.andreamarino.pharmazon.model.Feedback;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>{

   @Query("SELECT f FROM Feedback f WHERE f.client.username = :username")
    List<Feedback> findAllByClientUsername(@Param("username") String username);

    Optional <Feedback> findByCode(String code);
    
}
