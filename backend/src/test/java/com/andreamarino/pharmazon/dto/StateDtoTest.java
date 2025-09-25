package com.andreamarino.pharmazon.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;

@ExtendWith(MockitoExtension.class)
public class StateDtoTest {
    
    private StateDto stateDto;

    @Test
    public void conversionDto(){
        State state = new StatePreparation();
        stateDto = new StateDto(state);
    }

    @Test
    public void noArgsConstructor() {
        stateDto = new StateDto();
        assertNotNull(stateDto);
    }

    @Test
    public void getterAndSetter(){
        String state = "CONSEGNATO";
        
        stateDto = new StateDto();
        stateDto.setState(state);

        assertEquals(state, stateDto.getState());
    }

    @Test
    public void toStringMethod(){
        String state = "CONSEGNATO";
        
        stateDto = new StateDto();
        stateDto.setState(state);

        String toStringResult = stateDto.toString();
        String expectedToString = String.format("StateDto(state=%s)",
        state);

        assertEquals(toStringResult, expectedToString);
    }

    @Test
    void testEqualsAndHashCode() {
        String state = "CONSEGNATO";
        
        stateDto = new StateDto();
        stateDto.setState(state);

        StateDto sameStateDto = new StateDto();
        sameStateDto.setState(state);

        assertEquals(stateDto, sameStateDto);
        assertEquals(stateDto.hashCode(), sameStateDto.hashCode());

        sameStateDto.setState("PREPARAZIONE");

        assertNotEquals(stateDto, sameStateDto);
        assertNotEquals(stateDto.hashCode(), sameStateDto.hashCode());
    }
}
