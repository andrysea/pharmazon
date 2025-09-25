package com.andreamarino.pharmazon.model.designPattern.state;

import java.io.Serializable;
import java.util.List;
import com.andreamarino.pharmazon.dto.StateDto;
import com.andreamarino.pharmazon.model.Order;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type", discriminatorType = DiscriminatorType.STRING)
public abstract class State implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_state")
    private Long id;

    @Column(name = "state", nullable = false)
    private String state;

    @OneToMany(mappedBy = "state")
    private List<Order> orders;

    public State(Long id, String state){
        this.id = id;
        this.state = state;
    }

    public State(StateDto stateDto){
        this.state = stateDto.getState();
    }

    public Long getId(){
        return this.id;
    }

    public String getState(){
        return this.state;
    }

    public abstract void next(Order order);
    public abstract void previous(Order order);
    public abstract void delete(Order order);
}
