package com.andreamarino.pharmazon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StateDeleted;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.model.designPattern.state.StateInTransit;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.StateRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private final StateRepository stateRepository;

    public DataInitializer(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        State stateWatingApproval = new StateWaitingApproval();
        State statePreparation = new StatePreparation();
        State stateInTransit = new StateInTransit();
        State stateDelivered = new StateDelivered();
        State stateDeleted = new StateDeleted();

        insertIfNotExists(stateWatingApproval, stateWatingApproval.getState());
        insertIfNotExists(statePreparation, statePreparation.getState());
        insertIfNotExists(stateInTransit, stateInTransit.getState());
        insertIfNotExists(stateDelivered, stateDelivered.getState());
        insertIfNotExists(stateDeleted, stateDeleted.getState());
    }

    private void insertIfNotExists(State state, String stateText) {
        if (!stateRepository.findByState(stateText).isPresent()) {
            stateRepository.save(state);
        }
    }
}
