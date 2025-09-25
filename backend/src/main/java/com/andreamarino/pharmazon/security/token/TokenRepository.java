package com.andreamarino.pharmazon.security.token;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<Token, Long> {

  @Query(value = """
      SELECT t FROM Token t INNER JOIN User u\s
      ON t.user.id = u.id\s
      WHERE u.id = :id AND (t.expired = false OR t.revoked = false)\s
      """)
  List<Token> findAllValidTokenByUser(Long id);

  @Query("SELECT t FROM Token t WHERE t.user.username = :username AND t.expired = false AND t.revoked = false")
  Optional<Token> lastToken(@Param("username") String username);

  Optional<Token> findByToken(String token);
}

