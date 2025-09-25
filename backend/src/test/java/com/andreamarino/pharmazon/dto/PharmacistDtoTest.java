package com.andreamarino.pharmazon.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.model.Pharmacist;

@ExtendWith(MockitoExtension.class)
public class PharmacistDtoTest {

     private PharmacistDto pharmacistDto;

     @Test
     public void conversionDto(){
 
         String name = "Andrea";
         String surname = "Marino";
         String username = "andrysea";
         String number = "12345678901";
         String email = "user@user.com";
         String password = "12345678901";
         String birthdate = "2001-10-03";
         String tax_id = "1234567890123456";
 
         Pharmacist pharmacist = new Pharmacist();
 
         pharmacist.setName(name);
         pharmacist.setSurname(surname);
         pharmacist.setUsername(username);
         pharmacist.setNumber(number);
         pharmacist.setEmail(email);
         pharmacist.setPassword(password);
         pharmacist.setBirthDate(birthdate);
         pharmacist.setTaxId(tax_id);
 
         this.pharmacistDto = new PharmacistDto(pharmacist);
     }
    
}
