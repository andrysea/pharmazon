package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.dto.OrderDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_table")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Order implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @ManyToOne()
    @JoinColumn(name = "id_user", nullable = false) 
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_credit_card", nullable = false)
    private CreditCard creditCard;

    @Column(nullable = false)
    private Double total;

    @ManyToOne
    @JoinColumn(name = "id_cart", nullable = false)
    private Cart cart;
    
    @ManyToOne()
    @JoinColumn(name = "id_state", nullable = false) 
    private State state = new StatePreparation();

    @ManyToOne()
    @JoinColumn(name = "id_address", nullable = false) 
    private Address address = new Address();
    
    @CreationTimestamp
    private Timestamp dateTimeCreation;

    public Order(OrderDto orderDto){
        this.cart = new Cart(orderDto.getCartDto());
        this.code = orderDto.getCode();
        this.total = orderDto.getTotal();
        this.client = new Client(orderDto.getClientDto());
        this.address = new Address(orderDto.getAddressDto());
    }
}
