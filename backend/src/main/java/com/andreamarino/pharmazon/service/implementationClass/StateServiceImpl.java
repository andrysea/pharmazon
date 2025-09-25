package com.andreamarino.pharmazon.service.implementationClass;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.repository.StateRepository;
import com.andreamarino.pharmazon.service.interfaceForClass.StateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService{
    @Autowired
    private final StateRepository stateRepository;

    @Override
    public List<State> getStateList(){
        List<State> listState = stateRepository.findAllSteteinWaitingAndPreparation();
        if (listState.isEmpty()) {
            throw new NoSuchElementException();
        }
        return listState;
    } 
}
