package com.andreamarino.pharmazon.dto;
import com.andreamarino.pharmazon.model.Pharmacist;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PharmacistDto extends UserDto{
    
    public PharmacistDto(Pharmacist pharmacist){
        super(pharmacist); 
    }
}
