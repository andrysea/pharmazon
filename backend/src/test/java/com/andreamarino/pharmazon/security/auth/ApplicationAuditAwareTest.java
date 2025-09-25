package com.andreamarino.pharmazon.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.andreamarino.pharmazon.model.User;

@ExtendWith(MockitoExtension.class)
public class ApplicationAuditAwareTest {
    
    @InjectMocks
    private ApplicationAuditAware auditAware;

    @Test
    public void getCurrentAuditor_WhenAuthenticationNull_ReturnEmpty() {
        //Setup
        User user = new User();
        user.setId(1L);

        Authentication authentication = null;
        SecurityContext securityContext = mock(SecurityContext.class);
        
        //Mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        //Test
        Optional<Long> currentAuditor = auditAware.getCurrentAuditor();
        assertEquals(Optional.empty(), currentAuditor);
    }

    @Test
    public void getCurrentAuditor_WhenAuthenticationInstanceofAnonymousAuthenticationToken_ReturnEmpty() {
        //Setup
        User user = new User();
        user.setId(1L);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

        Authentication authentication = new AnonymousAuthenticationToken("key", "anonymousUser", authorities);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        //Mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        //Test
        Optional<Long> currentAuditor = auditAware.getCurrentAuditor();
        assertEquals(Optional.empty(), currentAuditor);
    }

    @Test
    public void getCurrentAuditor_WhenIsntAuthenticate_ReturnEmpty() {
        //Setup
        User user = new User();
        user.setId(1L);
        
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        //Mock
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        //Test
        Optional<Long> currentAuditor = auditAware.getCurrentAuditor();
        assertFalse(currentAuditor.isPresent());
    }

    @Test
    public void testGetCurrentAuditor_WhenAuthenticated() {
        //Setup
        Authentication authentication = mock(Authentication.class);    
        SecurityContext securityContext = mock(SecurityContext.class);
        
        User user = new User();
        user.setId(123L);

        //Mock
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        //Test
        ApplicationAuditAware auditAware = new ApplicationAuditAware();
        Optional<Long> currentAuditor = auditAware.getCurrentAuditor();

        assertEquals(Optional.of(123L), currentAuditor);
    }
}
