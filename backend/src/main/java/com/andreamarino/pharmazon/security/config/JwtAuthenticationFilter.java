package com.andreamarino.pharmazon.security.config;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.token.TokenRepository;
import com.andreamarino.pharmazon.security.user.Role;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(
      @Nullable HttpServletRequest request,
      @Nullable HttpServletResponse response,
      @Nullable FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/auth")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    username = jwtService.extractUsername(jwt);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
      User user = userRepository.findByUsername(username).get();

      
      if(user.getRole().equals(Role.CLIENT)){
        if(request.getParameter("username")!= null ){
          String usernameRequest  = request.getParameter("username");
          if (request.getParameter("username").chars().anyMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("Lo username inserito non puo' contenere caratteri maiuscoli.");
          }
          if(!usernameRequest.equals(user.getUsername())){
            throw new IllegalArgumentException("Lo username legato al token e lo username passato come parametro, sono differenti.");
          }
        }        
      }

      var isTokenValid = tokenRepository.findByToken(jwt)
          .map(t -> !t.isExpired() && !t.isRevoked())
          .orElse(false);
      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
