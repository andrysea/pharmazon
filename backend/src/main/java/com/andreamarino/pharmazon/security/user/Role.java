package com.andreamarino.pharmazon.security.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.List;

public enum Role {
  CLIENT,
  ADMIN;

  public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
  }
}
