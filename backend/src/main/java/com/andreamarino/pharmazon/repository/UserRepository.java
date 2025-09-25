package com.andreamarino.pharmazon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.security.user.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
  Optional<User> findByTaxId(String taxId);
  Optional<User> findByNumber(String number);
  List<User> findByRole(Role role);
}
