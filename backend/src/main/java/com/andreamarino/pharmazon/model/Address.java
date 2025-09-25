package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import com.andreamarino.pharmazon.dto.AddressDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Address implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_address") 
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String cap;

    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Client client;

    @CreationTimestamp
    private Timestamp dateTimeCreation;

    public Address(AddressDto addressDto){
        this.code = addressDto.getCode();
        this.name = addressDto.getName();
        this.surname = addressDto.getSurname();
        this.number = addressDto.getNumber();
        this.address = addressDto.getAddress();
        this.cap = addressDto.getCap();
        this.city = addressDto.getCity();
        this.province = addressDto.getProvince();
    }
}
