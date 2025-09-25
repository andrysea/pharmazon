package com.andreamarino.pharmazon.model.designPattern.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.andreamarino.pharmazon.model.Order;

@Entity
@DiscriminatorValue("PREPARATION")
public class StatePreparation extends State{

    public StatePreparation(){
        super(2L, "IN PREPARAZIONE");
    }

    @Override
    public void next(Order order) {
        order.setState(new StateInTransit());
    }

    @Override
    public void previous(Order order) {
        System.out.println("STATO ATTUALE: " + order.getState().getState() + ". NON E' POSSIBILE RITORNARE ALLO STATO PRECEDENTE.");
    }

    @Override
    public void delete(Order order) {
        order.setState(new StateDeleted());
    }
}
