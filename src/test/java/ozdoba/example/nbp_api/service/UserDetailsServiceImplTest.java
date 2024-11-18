package ozdoba.example.nbp_api.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ozdoba.example.nbp_api.model.AppUser;
import ozdoba.example.nbp_api.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        String username = "testUser";
        String password = "password123";
        String role = "ROLE_USER";

        AppUser mockUser = AppUser.builder()
                .username(username)
                .password(password)
                .role(role)
                .firstName("Test")
                .lastName("User")
                .initialBalancePLN(BigDecimal.valueOf(100))
                .initialBalanceUSD(BigDecimal.ZERO)
                .accountId("12345")
                .build();

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(role)));
    }


    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        // Given
        String username = "nonExistentUser";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username));

        assertEquals("User not found with username: " + username, exception.getMessage());
    }
}