package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.andreamarino.pharmazon.dto.CartItemDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
public class CartItem implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cart_item")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_cart", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @Lob
    @Column(name = "imagePrescription", columnDefinition = "LONGBLOB")
    private byte[] imagePrescription;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks = new ArrayList<>();

    @Column(nullable = false) 
    private String code;

    @Column(nullable = false) 
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false) 
    private Integer quantity;

    @Column(nullable = false) 
    private boolean flag = false;

    @CreationTimestamp
    private Timestamp dateTimeCreation;

    public CartItem(CartItemDto cartItemDto){
        this.product = new Product(cartItemDto.getProductDto());
        this.quantity = cartItemDto.getQuantity();
        this.code = cartItemDto.getCode();
        this.name = cartItemDto.getName();
        this.price = cartItemDto.getPrice();
        this.imagePrescription = cartItemDto.getImagePrescription();
    }
}
