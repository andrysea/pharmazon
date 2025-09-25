package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.CreditCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDto {
    private String name;
    private String surname;
    private String number;
    private String cardSecurityCode;
    private String expirationDate;
    private Double balance;
    
    public CreditCardDto(CreditCard creditCard){
        this.name = creditCard.getName();
        this.surname = creditCard.getSurname();
        this.number = creditCard.getNumber();
        this.cardSecurityCode = creditCard.getCardSecurityCode();
        this.expirationDate = creditCard.getExpirationDate().toString();
        this.balance = creditCard.getBalance();
    }
}
