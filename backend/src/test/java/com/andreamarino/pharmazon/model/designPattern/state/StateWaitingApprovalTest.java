package com.andreamarino.pharmazon.model.designPattern.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Order;

@ExtendWith(MockitoExtension.class)
public class StateWaitingApprovalTest {

    private State state;

    @Test
    public void noArgsConstructor(){
        state = new StateWaitingApproval(); 
    }

    @Test
    public void getter(){
        Long id = 1L;
        String stateString = "IN ATTESA DI APPROVAZIONE";
        state = new StateWaitingApproval();
        
        assertEquals(id, state.getId());
        assertEquals(stateString, state.getState());
    }

    @Test
    public void next_WhenValidInput_Success(){
        //Setup
        Order order = new Order();

        //Test
        state = new StateWaitingApproval();
        state.next(order);
    }

    @Test
    public void previous_WhenValidInput_Success(){
        //Setup
        Order order = new Order();

        //Test
        state = new StateWaitingApproval();
        state.previous(order);
    }

    @Test
    public void delete_WhenValidInput_Success(){
        //Setup
        Order order = new Order();

        //Test
        state = new StateWaitingApproval();
        state.previous(order);
    }
}