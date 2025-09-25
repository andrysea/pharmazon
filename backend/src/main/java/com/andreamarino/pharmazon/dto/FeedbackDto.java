package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
    private String code;
    private String description;
    private CartItemDto cartItemDto;
    private ClientDto clientDto;

    public FeedbackDto(Feedback feedback){
        this.code = feedback.getCode();
        this.cartItemDto = new CartItemDto(feedback.getCartItem());
        this.description = feedback.getDescription();
        this.clientDto = new ClientDto(feedback.getClient());
    }
}
