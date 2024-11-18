package ozdoba.example.nbp_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ozdoba.example.nbp_api.model.AccountDetailsResponse;
import ozdoba.example.nbp_api.model.AppUser;
import ozdoba.example.nbp_api.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddUserSuccessfully() {
        // Given
        AppUser newUser = AppUser.builder()
                .username("testUser")
                .password("rawPassword")
                .firstName("John")
                .lastName("Doe")
                .initialBalancePLN(BigDecimal.valueOf(1000))
                .role("ROLE_USER")
                .build();

        when(userRepository.existsByUsername(newUser.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");

        // When
        userService.addUser(newUser);

        // Then
        assertNotNull(newUser.getAccountId());
        assertEquals("encodedPassword", newUser.getPassword());
        verify(userRepository).save(newUser);
    }

    @Test
    void shouldThrowExceptionWhenAddingUserWithExistingUsername() {
        // Given
        AppUser existingUser = AppUser.builder()
                .username("existingUser")
                .build();

        when(userRepository.existsByUsername(existingUser.getUsername())).thenReturn(true);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.addUser(existingUser));
        assertEquals("Username is already in use", exception.getMessage());
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void shouldFetchAccountDetailsSuccessfully() {
        // Given
        String accountId = UUID.randomUUID().toString();
        AppUser mockUser = AppUser.builder()
                .accountId(accountId)
                .username("testUser")
                .firstName("John")
                .lastName("Doe")
                .initialBalancePLN(BigDecimal.valueOf(1000))
                .initialBalanceUSD(BigDecimal.valueOf(200))
                .build();

        when(userRepository.findByAccountId(accountId)).thenReturn(Optional.of(mockUser));

        // When
        AccountDetailsResponse response = userService.getAccountDetails(accountId);

        // Then
        assertNotNull(response);
        assertEquals(mockUser.getAccountId(), response.getAccountId());
        assertEquals(mockUser.getFirstName(), response.getFirstName());
        assertEquals(mockUser.getLastName(), response.getLastName());
    }

    @Test
    void shouldThrowExceptionWhenFetchingAccountDetailsForNonExistentAccount() {
        // Given
        String nonExistentAccountId = UUID.randomUUID().toString();
        when(userRepository.findByAccountId(nonExistentAccountId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.getAccountDetails(nonExistentAccountId));
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void shouldUpdateBalancesForPlnToUsdSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        AppUser mockUser = AppUser.builder()
                .accountId(accountId.toString())
                .initialBalancePLN(BigDecimal.valueOf(1000))
                .initialBalanceUSD(BigDecimal.valueOf(100))
                .build();

        BigDecimal amountPln = BigDecimal.valueOf(200);
        BigDecimal amountUsd = BigDecimal.valueOf(50);

        when(userRepository.findByAccountId(accountId.toString())).thenReturn(Optional.of(mockUser));

        // When
        userService.updateBalancesForPlnToUsd(accountId, amountPln, amountUsd);

        // Then
        verify(userRepository).save(mockUser);
        assertEquals(BigDecimal.valueOf(800), mockUser.getInitialBalancePLN());
        assertEquals(BigDecimal.valueOf(150), mockUser.getInitialBalanceUSD());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientPlnBalanceForUpdate() {
        // Given
        UUID accountId = UUID.randomUUID();
        AppUser mockUser = AppUser.builder()
                .accountId(accountId.toString())
                .initialBalancePLN(BigDecimal.valueOf(100))
                .build();

        BigDecimal amountPln = BigDecimal.valueOf(200);
        BigDecimal amountUsd = BigDecimal.valueOf(50);

        when(userRepository.findByAccountId(accountId.toString())).thenReturn(Optional.of(mockUser));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateBalancesForPlnToUsd(accountId, amountPln, amountUsd));
        assertEquals("Insufficient balance in PLN", exception.getMessage());
    }

    @Test
    void shouldUpdateBalancesForUsdToPlnSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();
        AppUser mockUser = AppUser.builder()
                .accountId(accountId.toString())
                .initialBalancePLN(BigDecimal.valueOf(1000))
                .initialBalanceUSD(BigDecimal.valueOf(100))
                .build();

        BigDecimal amountUsd = BigDecimal.valueOf(50);
        BigDecimal amountPln = BigDecimal.valueOf(200);

        when(userRepository.findByAccountId(accountId.toString())).thenReturn(Optional.of(mockUser));

        // When
        userService.updateBalancesForUsdToPln(accountId, amountUsd, amountPln);

        // Then
        verify(userRepository).save(mockUser);
        assertEquals(BigDecimal.valueOf(1200), mockUser.getInitialBalancePLN());
        assertEquals(BigDecimal.valueOf(50), mockUser.getInitialBalanceUSD());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientUsdBalanceForUpdate() {
        // Given
        UUID accountId = UUID.randomUUID();
        AppUser mockUser = AppUser.builder()
                .accountId(accountId.toString())
                .initialBalanceUSD(BigDecimal.valueOf(30))
                .build();

        BigDecimal amountUsd = BigDecimal.valueOf(50);
        BigDecimal amountPln = BigDecimal.valueOf(200);

        when(userRepository.findByAccountId(accountId.toString())).thenReturn(Optional.of(mockUser));

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateBalancesForUsdToPln(accountId, amountUsd, amountPln));
        assertEquals("Insufficient balance in USD", exception.getMessage());
    }
}
