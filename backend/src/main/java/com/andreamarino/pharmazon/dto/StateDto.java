package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.designPattern.state.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateDto {
    private String state;

    public StateDto(State state){
        this.state = state.getState();
    }
}
