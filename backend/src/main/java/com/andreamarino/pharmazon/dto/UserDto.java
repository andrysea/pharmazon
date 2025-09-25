package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.security.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto{
    private Role role;
    private String name;
    private String surname;
    private String username;
    private String number;
    private String email;
    private String password;
    private String birthDate;
    private String taxId;

    public UserDto(User user){
      this.role = user.getRole();
      this.name = user.getName();
      this.surname = user.getSurname();
      this.username = user.getUsername();
      this.number = user.getNumber();
      this.email = user.getEmail();
      this.password = user.getPassword();
      this.birthDate = user.getBirthDate();
      this.taxId = user.getTaxId();
    }
}
