package com.andreamarino.pharmazon.dto;

import java.util.List;
import java.util.stream.Collectors;
import com.andreamarino.pharmazon.model.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientDto extends UserDto{
    
    private List<CreditCardDto> creditCardsDto;

    public ClientDto(Client client) {
        super(client); 
        this.creditCardsDto = client.getCreditCards().stream().map(entity -> new CreditCardDto(entity)).collect(Collectors.toList());
    }
}
