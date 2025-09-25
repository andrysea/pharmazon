package com.andreamarino.pharmazon.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.service.interfaceForClass.StateService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class StateController {
    
    private final StateService stateService;
    
    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista degli stati disponibili.
     * 
     * @return una ResponseEntity contenente la lista degli oggetti di tipo State, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getListState(){
        List<State> listState = new ArrayList<>();
        listState = stateService.getStateList();
        return ResponseEntity.status(HttpStatus.OK).body(listState);
    }
}
