package com.andreamarino.pharmazon.model.designPattern.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.andreamarino.pharmazon.model.Order;

@Entity
@DiscriminatorValue("DELIVERED")
public class StateDelivered extends State{

    public StateDelivered(){
        super(4L, "CONSEGNATO");
    }

    @Override
    public void next(Order order) {
        System.out.println("STATO ATTUALE: " + order.getState().getState() + ". NESSUNO STATO SUCCESSIVO.");
    }

    @Override
    public void previous(Order order) {
        order.setState(new StateInTransit());
    }

    @Override
    public void delete(Order order) {
        System.out.println("STATO ATTUALE: " + order.getState().getState() + ". NON SI PUO' CANCELLARE, ORDINE ORMAI CONSEGNATO.");
    }
}
