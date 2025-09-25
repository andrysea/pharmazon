package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.security.user.ChangePasswordRequest;

public interface UserService {
    List<UserDto> getUserListDto();
    UserDto findByUsername(String username);
    UserDto updateUserDto(UserDto userDto, String username);
    void updatePassword(ChangePasswordRequest changePasswordRequest, UserDetails userDetails);
    void deleteUserDto(String username);
    void sendEmail(String email);
}
