package com.andreamarino.pharmazon.model.designPattern.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Order;

@ExtendWith(MockitoExtension.class)
public class StateDeletedTest {

    private State state;

    @Test
    public void noArgsConstructor(){
        state = new StateDeleted(); 
    }

    @Test
    public void getter(){
        Long id = 5L;
        String stateString = "ELIMINATO";
        state = new StateDeleted();
        
        assertEquals(id, state.getId());
        assertEquals(stateString, state.getState());
    }

    @Test
    public void next_WhenValidInput_Success(){
        //Setup
        Order order = new Order();

        //Test
        state = new StateDeleted();
        state.next(order);
    }

    @Test
    public void previous_WhenValidInput_Success(){
        //Setup
        Order order = new Order();

        //Test
        state = new StateDeleted();
        state.previous(order);
    }

    @Test
    public void delete_WhenValidInput_Success(){
        //Setup
        Order order = new Order();

        //Test
        state = new StateDeleted();
        state.delete(order);
    }
}
