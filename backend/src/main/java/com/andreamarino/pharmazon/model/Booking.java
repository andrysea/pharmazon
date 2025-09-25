package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.andreamarino.pharmazon.dto.BookingDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Scope("prototype")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Booking implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking") 
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    
    @ManyToOne()
    @JoinColumn(name = "id_service", nullable = false) 
    private ServiceClass service;
    
    @ManyToOne()
    @JoinColumn(name = "id_user", nullable = false) 
    private Client client;

    @CreationTimestamp
    private Timestamp dateTimeCreation;
    
    @Column(nullable = false) 
    private Boolean accepted = false;

    public Booking(BookingDto bookingDto){
        this.code = bookingDto.getCode();
        this.dateTimeCreation = bookingDto.getDateTimeCreation();
        this.service = new ServiceClass(bookingDto.getService());
        this.accepted = bookingDto.getAccepted();
    }
}
