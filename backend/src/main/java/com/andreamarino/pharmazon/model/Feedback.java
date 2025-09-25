package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;
import com.andreamarino.pharmazon.dto.FeedbackDto;
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

@Entity
@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review") 
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_cart_item", nullable = false)
    private CartItem cartItem;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Client client;

    @CreationTimestamp
    private Timestamp dateTimeCreation;

    public Feedback(FeedbackDto feedbackDto){
        this.code = feedbackDto.getCode();
        this.cartItem = new CartItem(feedbackDto.getCartItemDto());
        this.description = feedbackDto.getDescription();
        this.client = new Client(feedbackDto.getClientDto());
    }
}
