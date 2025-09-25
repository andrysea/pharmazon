package com.andreamarino.pharmazon.services.chat.model;

import com.andreamarino.pharmazon.dto.ClientDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String code;
    private ClientDto clientDto;
    private Boolean activeChat;
    private Boolean accepted;

    public ChatDto(Chat chat){
        this.code = chat.getCode();
        this.clientDto = new ClientDto(chat.getClient());
        this.activeChat = chat.getActiveChat();
        this.accepted = chat.getAccepted();
    }
}