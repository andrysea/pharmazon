package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.andreamarino.pharmazon.dto.CartDto;
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
public class Cart implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cart")
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>(); 

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>(); 

    @ManyToOne()
    @JoinColumn(name = "id_user", nullable = false)
    private Client client;

    @Column(nullable = false)
    private Boolean activeCart = true;

    public Cart(CartDto cartDto){
        this.cartItems = cartDto.getCartItemsDto().stream().map(entity -> new CartItem(entity)).collect(Collectors.toList());
        this.client = new Client(cartDto.getClientDto());
    }
}