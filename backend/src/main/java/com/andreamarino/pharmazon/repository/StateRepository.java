package com.andreamarino.pharmazon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long>{

    @Query("SELECT s FROM State s WHERE s.state NOT IN ('WAITING_APPROVAL', 'PREPARATION')")
    List<State> findAllSteteinWaitingAndPreparation();

    Optional <State> findByState(String state);

}
