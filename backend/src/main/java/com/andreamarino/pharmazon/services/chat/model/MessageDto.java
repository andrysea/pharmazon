package com.andreamarino.pharmazon.services.chat.model;

import com.andreamarino.pharmazon.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageDto {
    private UserDto userDto;
    private String message;
    private boolean lastMessage = false;
}
