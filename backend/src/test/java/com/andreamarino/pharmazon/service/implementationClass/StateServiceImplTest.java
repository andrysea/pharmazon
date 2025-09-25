package com.andreamarino.pharmazon.service.implementationClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.StateRepository;

@ExtendWith(MockitoExtension.class)
public class StateServiceImplTest {
    
    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateServiceImpl stateServiceImpl;

    @Test
    void getStateListinWaitingAndPreparation_WhenArePresent_ReturnList() {
        //Setup
        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        StatePreparation statePreparation = new StatePreparation();

        List<State> listState = new ArrayList<>();
        listState.add(stateWatingApproval);
        listState.add(statePreparation);

        //Mock
        when(stateRepository.findAllSteteinWaitingAndPreparation()).thenReturn(listState);

        //Test
        List<State> result = stateServiceImpl.getStateList();
        assertEquals(listState.size(), result.size());
        verify(stateRepository, times(1)).findAllSteteinWaitingAndPreparation();
    }

    @Test
    void getStateListinWaitingAndPreparation_EmptyList_NoSuchElementException() {
       //Setup
       List<State> listState = new ArrayList<>();
      
       //Mock   
       when(stateRepository.findAllSteteinWaitingAndPreparation()).thenReturn(listState);

       //Test
        assertThrows(NoSuchElementException.class,
            () -> stateServiceImpl.getStateList());
        
        verify(stateRepository, times(1)).findAllSteteinWaitingAndPreparation();
    }
}
