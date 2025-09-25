package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.andreamarino.pharmazon.dto.ServiceDto;
import com.andreamarino.pharmazon.model.designPattern.observer.service.ObserverService;
import com.andreamarino.pharmazon.model.designPattern.observer.service.Subject;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

@Component
@Scope("prototype")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class ServiceClass implements Serializable, Subject{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service") 
    private Long id;
    
    @Column(unique = true, nullable = false) 
    private String code;
    
    @Column(nullable = false) 
    private String name;
    
    @Column(nullable = false) 
    private String description;
    
    @Column(nullable = false) 
    private Double price;
    
    @Column(nullable = false) 
    private LocalDateTime dateChosen;

    @Column(nullable = false)
    private Integer availability;

    @Lob
    @Column(name = "image_bytes", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] image;
    
    @OneToMany(mappedBy = "service",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "id_user", nullable = false) 
    private Pharmacist pharmacist;

    @Transient
    private List<ObserverService> observersClientService = new ArrayList<>();
    
    @CreationTimestamp
    private Timestamp dateTimeCreation;

    @Override
    public void addObserver(ObserverService observer) {
        this.observersClientService.add(observer);
    }

    @Override
    public void removeObserver(ObserverService observer) {
        this.observersClientService.remove(observer);
    }

    @Override
    public void notifyObservers(EmailService emailService, String nameService, String date) {
        for (ObserverService observer : this.observersClientService) {
            observer.updateService(emailService, nameService, date);
        }
        this.bookings.clear();
    }

    public ServiceClass(ServiceDto serviceDto){
        this.code = serviceDto.getCode();
        this.name = serviceDto.getName();
        this.description = serviceDto.getDescription();
        this.price = serviceDto.getPrice();
        this.availability = serviceDto.getAvailability();
        this.image = serviceDto.getImage();
        this.dateChosen = serviceDto.getDateChosen();
    }
}

