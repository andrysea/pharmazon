package com.andreamarino.pharmazon.configuration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.designPattern.state.StateDeleted;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.model.designPattern.state.StateInTransit;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.StateRepository;

@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {

    @InjectMocks
    private DataInitializer dataInitializer;

    @Mock
    private StateRepository stateRepository;

    @Test
    public void DataInitializer_WhenValidInput_Success(){
        //Setup
        dataInitializer = new DataInitializer(stateRepository);
    }

    @Test
    public void run_WhenAllEmpty_Success() throws Exception{
        //Setup
        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        StatePreparation statePreparation = new StatePreparation();
        StateInTransit stateInTransit = new StateInTransit();
        StateDelivered stateDelivered = new StateDelivered();
        StateDeleted stateDeleted = new StateDeleted();
        
        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.empty());
        
        //Test
        this.dataInitializer.run();

        verify(stateRepository, times(1)).findByState(stateWatingApproval.getState());
        verify(stateRepository, times(1)).findByState(statePreparation.getState());
        verify(stateRepository, times(1)).findByState(stateInTransit.getState());
        verify(stateRepository, times(1)).findByState(stateDelivered.getState());
        verify(stateRepository, times(1)).findByState(stateDeleted.getState());
        
        verify(stateRepository).save(any(StateWaitingApproval.class));
        verify(stateRepository).save(any(StatePreparation.class));
        verify(stateRepository).save(any(StateInTransit.class));
        verify(stateRepository).save(any(StateDelivered.class));
        verify(stateRepository).save(any(StateDeleted.class));
    }

    @Test
    public void run_WhenNoEmpty_Success() throws Exception{
        //Setup
        StateWaitingApproval stateWatingApproval = new StateWaitingApproval();
        StatePreparation statePreparation = new StatePreparation();
        StateInTransit stateInTransit = new StateInTransit();
        StateDelivered stateDelivered = new StateDelivered();
        StateDeleted stateDeleted = new StateDeleted();
        
        //Mock
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateWatingApproval));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(statePreparation));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateInTransit));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDelivered));
        when(stateRepository.findByState(anyString())).thenReturn(Optional.of(stateDeleted));
        
        //Test
        this.dataInitializer.run();
        verify(stateRepository, times(1)).findByState(stateWatingApproval.getState());
        verify(stateRepository, times(1)).findByState(statePreparation.getState());
        verify(stateRepository, times(1)).findByState(stateInTransit.getState());
        verify(stateRepository, times(1)).findByState(stateDelivered.getState());
        verify(stateRepository, times(1)).findByState(stateDeleted.getState());
    }

}
