package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import com.andreamarino.pharmazon.dto.CreditCardDto;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class CreditCard implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credit_card") 
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false, unique=true)
    private String number;
    @Column(nullable = false)
    private String cardSecurityCode;
    @Column(nullable = false)
    private String expirationDate;
    @Column(nullable = false)
    private Double balance;    
    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>(); 
    
    @CreationTimestamp
    private Timestamp dateTimeCreation;

    public CreditCard(CreditCardDto creditCardDto){
        this.name = creditCardDto.getName();
        this.surname = creditCardDto.getSurname();
        this.number = creditCardDto.getNumber();
        this.cardSecurityCode = creditCardDto.getCardSecurityCode();
        this.expirationDate = creditCardDto.getExpirationDate();
        this.balance = creditCardDto.getBalance();
    }
}
