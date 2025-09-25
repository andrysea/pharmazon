package com.andreamarino.pharmazon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.Booking;
import com.andreamarino.pharmazon.model.Client;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{

    Optional <Booking> findByCode(String code);

    @Query("SELECT b FROM Booking b WHERE b.accepted = false")
    List<Booking> findBookingNotAccepted();

    @Query("SELECT b FROM Booking b WHERE b.accepted = true")
    List<Booking> findBookingAccepted();

    @Query("SELECT b.client FROM Booking b WHERE b.service.id = :serviceId")
    List<Client> findClientsByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT b FROM Booking b WHERE b.service.id = :serviceId AND b.client.id = :clientId")
    Optional<Booking> findByServiceAndClient(@Param("serviceId") Long serviceId, @Param("clientId") Long clientId);

    boolean existsByClientUsernameAndAcceptedFalse(String username);
}
