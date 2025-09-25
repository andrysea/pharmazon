package com.andreamarino.pharmazon.model.designPattern.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import com.andreamarino.pharmazon.model.Order;

@Entity
@DiscriminatorValue("WAITING_APPROVAL")
public class StateWaitingApproval extends State{

    public StateWaitingApproval(){
        super(1L, "IN ATTESA DI APPROVAZIONE");
    }
    
    @Override
    public void next(Order order) {
        order.setState(new StatePreparation());
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
