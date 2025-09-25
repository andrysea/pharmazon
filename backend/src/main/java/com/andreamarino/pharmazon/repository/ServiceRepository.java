package com.andreamarino.pharmazon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.ServiceClass;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceClass, Long>{
    Optional<ServiceClass> findByCode(String code);
    Optional<ServiceClass> findByName(String name);
    Optional<ServiceClass> findByCodeAndName (String code, String name);
    
    List<ServiceClass> findByDateChosenAfter(LocalDateTime currentDate);

    @Query("SELECT s FROM ServiceClass s WHERE s.name LIKE CONCAT('%',:name,'%')")
    List<ServiceClass> findByNameList(@Param("name")String name);

    @Query("SELECT s FROM ServiceClass s WHERE s.name LIKE CONCAT('%', :name, '%') AND s.dateChosen > :currentDate")
    List<ServiceClass> findByNameListAndDateChosenAfter(@Param("name") String name, @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(s) > 0 FROM ServiceClass s WHERE s.name = :name AND YEAR(s.dateChosen) = YEAR(:dateChosen) AND MONTH(s.dateChosen) = MONTH(:dateChosen) AND DAY(s.dateChosen) = DAY(:dateChosen)")
    boolean existsByNameAndDateChosen(@Param("name") String name, @Param("dateChosen") LocalDateTime dateChosen);
}
