package com.andreamarino.pharmazon.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.andreamarino.pharmazon.dto.UserDto;
import com.andreamarino.pharmazon.security.token.Token;
import com.andreamarino.pharmazon.security.user.Role;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.InheritanceType;

@Component
@Scope("prototype")
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user") 
    private Long id;

    @Column(name = "role", nullable = false) 
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name = "name", nullable = false) 
    private String name;

    @Column(name = "surname", nullable = false) 
    private String surname;

    @Column(name = "username", unique = true, nullable = false) 
    private String username;

    @Column(name = "email", unique = true, nullable = false) 
    private String email;

    @Column(name = "number", unique = true, nullable = false) 
    private String number;

    @Column(name = "taxId", unique = true, nullable = false, length = 20) 
    private String taxId;

    @Column(name = "password", nullable = false) 
    private String password;

    @Column(name = "birthdate", nullable = false) 
    private String birthDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Token> tokens = new ArrayList<>();

    @CreationTimestamp
    private Timestamp dateTimeCreation;

    public User(User user) {
      this.role = user.getRole();
      this.id = user.getId();
      this.name = user.getName();
      this.surname = user.getSurname();
      this.username = user.getUsername();
      this.email = user.getEmail();
      this.number = user.getNumber();
      this.password = user.getPassword();
      this.birthDate = user.getBirthDate();
      this.taxId = user.getTaxId();
      this.tokens = user.getTokens();
    }

    public User(UserDto userDto){
      this.role = userDto.getRole();
      this.name = userDto.getName();
      this.surname = userDto.getSurname();
      this.username = userDto.getUsername();
      this.email = userDto.getEmail();
      this.password = userDto.getPassword();
      this.birthDate = userDto.getBirthDate();
      this.taxId = userDto.getTaxId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return role.getAuthorities();
    }
  
    @Override
    public String getPassword() {
      return password;
    }
  
    @Override
    public String getUsername() {
      return username;
    }
  
    @Override
    public boolean isAccountNonExpired() {
      return true;
    }
  
    @Override
    public boolean isAccountNonLocked() {
      return true;
    }
  
    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }
  
    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [id=").append(id)
              .append(", role=").append(role)
              .append(", name=").append(name)
              .append(", surname=").append(surname)
              .append(", username=").append(username)
              .append(", email=").append(email)
              .append(", number=").append(number)
              .append(", taxId=").append(taxId)
              .append(", password=").append(password)
              .append(", birthDate=").append(birthDate)
              .append(", tokens=[");
        
        for (Token token : tokens) {
            builder.append(token.getId()).append(", ");
        }
        if (!tokens.isEmpty()) {
            builder.delete(builder.length() - 2, builder.length());
        }
        
        builder.append("],");
        
        builder.append(" dateTimeCreation=").append(dateTimeCreation)
              .append("]");
        
        return builder.toString();
    }

}
