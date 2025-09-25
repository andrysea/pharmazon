package com.andreamarino.pharmazon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.service.interfaceForClass.StateService;

@ExtendWith(MockitoExtension.class)
public class StateControllerTest {

    @Mock
    private StateService stateService;

    @InjectMocks
    private StateController stateController;

    @Test
    public void getListState_WhenValid_ResponseOk(){
        //Setup
        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        StatePreparation statePreparation = new StatePreparation();

        List<State> listState = new ArrayList<>();
        listState.add(stateWatingApproval);
        listState.add(statePreparation);

        //Mock
        when(stateService.getStateList()).thenReturn(listState);

        //Test
        ResponseEntity<?> response = stateController.getListState();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listState, response.getBody());
        verify(stateService, times(1)).getStateList();
    }
    
}
