package com.andreamarino.pharmazon.model.designPattern.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.andreamarino.pharmazon.model.Order;

@Entity
@DiscriminatorValue("IN_TRANSIT")
public class StateInTransit extends State{

    public StateInTransit(){
        super(3L, "IN TRANSITO");
    }

    @Override
    public void next(Order order) {
        order.setState(new StateDelivered());
    }

    @Override
    public void previous(Order order) {
        order.setState(new StatePreparation());
    }

    @Override
    public void delete(Order order) {
        order.setState(new StateDeleted());
    }
}
