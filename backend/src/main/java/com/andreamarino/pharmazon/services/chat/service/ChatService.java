package com.andreamarino.pharmazon.services.chat.service;

import com.andreamarino.pharmazon.services.chat.model.ChatDto;

public interface ChatService {
    ChatDto insertChat(String username);
    void updateChatsWaiting(String username);
    ChatDto getChatAccepted(String username);
    ChatDto getChatWaiting(String username);
    void closeChat(String username);
}
