package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.CreditCardDto;

public interface CreditCardService {
    CreditCardDto insertCreditCardDto(CreditCardDto creditCardDto, String username);
    CreditCardDto updateCreditCardDto(CreditCardDto creditCardDto, String username);
    void deactivateCreditCardDto(String number, String username);
    List<CreditCardDto> getCreditCardDto(String username);
}
