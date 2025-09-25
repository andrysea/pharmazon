package com.andreamarino.pharmazon.services.chat.model;

import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Pharmacist;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat") 
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne()
    @JoinColumn(name = "id_client", nullable = false) 
    private Client client;
    
    @ManyToOne()
    @JoinColumn(name = "id_pharmacist") 
    private Pharmacist pharmacist;

    @Column(nullable = false)
    private Boolean activeChat = false;

    @Column(nullable = false)
    private Boolean accepted = false;

    @CreationTimestamp
    private Timestamp dateTimeCreation;

    public Chat(ChatDto chatDto){
        this.code = chatDto.getCode();
        this.client = new Client(chatDto.getClientDto());
        this.activeChat = chatDto.getActiveChat();
        this.accepted = chatDto.getAccepted();
    }
}
