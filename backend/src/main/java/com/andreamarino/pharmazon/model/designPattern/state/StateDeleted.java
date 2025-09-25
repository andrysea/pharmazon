package com.andreamarino.pharmazon.model.designPattern.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.andreamarino.pharmazon.model.Order;

@Entity
@DiscriminatorValue("DELETED")
public class StateDeleted extends State{
    
    public StateDeleted(){
        super(5L, "ELIMINATO");
    }

    @Override
    public void next(Order order) {
        System.out.println("STATO ATTUALE: " + order.getState().getState() + ". NESSUNO STATO SUCCESSIVO.");
    }

    @Override
    public void previous(Order order) {
        System.out.println("STATO ATTUALE: " + order.getState().getState() + ". NON E' POSSIBILE RITORNARE ALLO STATO PRECEDENTE.");
    }

    @Override
    public void delete(Order order) {
        System.out.println("STATO ATTUALE: " + order.getState().getState() + ". SEI GIA' NELLO STATO ELIMINATO.");
    }
}
