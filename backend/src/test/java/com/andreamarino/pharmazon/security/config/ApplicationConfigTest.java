package com.andreamarino.pharmazon.security.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    public void userDetailsService_WhenValid_ReturnObject(){
        //Setup
        String username = "andrysea";
        User user = new User();
        user.setUsername(username);

        //Test
        UserDetailsService userReturned = applicationConfig.userDetailsService();
        assertNotNull(userReturned);
    }

    @Test
    public void authenticationProvider_WhenValid_ReturnObject() {
        //Setup
        AuthenticationProvider authenticationProvider = applicationConfig.authenticationProvider();
        
        //Test
        assertNotNull(authenticationProvider);
    }

     @Test
    public void auditorAware_WhenValid_ReturnObject() {
        //Setup
        AuditorAware<Long> auditorAware = applicationConfig.auditorAware();
        
        //Test
        assertNotNull(auditorAware);
    }

    @Test
    public void passwordEncoder_WhenValid_ReturnObject() {
        //Setup
        PasswordEncoder passwordEncoder = applicationConfig.passwordEncoder();
        
        //Test
        assertNotNull(passwordEncoder);
    }
    
}
