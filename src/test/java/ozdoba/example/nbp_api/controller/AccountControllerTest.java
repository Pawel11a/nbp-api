package ozdoba.example.nbp_api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ozdoba.example.nbp_api.model.AccountDetailsResponse;
import ozdoba.example.nbp_api.model.ExchangeRequest;
import ozdoba.example.nbp_api.service.ExchangeRateService;
import ozdoba.example.nbp_api.service.UserService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    private ExchangeRateService exchangeRateService;
    private UserService userService;
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        exchangeRateService = mock(ExchangeRateService.class);
        userService = mock(UserService.class);
        accountController = new AccountController(exchangeRateService, userService);
    }

    @Test
    void testExchangePlnToUsd() {
        // given
        UUID accountId = UUID.randomUUID();
        ExchangeRequest request = new ExchangeRequest();
        request.setAmount(BigDecimal.valueOf(1000));

        when(exchangeRateService.convertPlnToUsd(BigDecimal.valueOf(1000))).thenReturn(BigDecimal.valueOf(250));
        doNothing().when(userService).updateBalancesForPlnToUsd(accountId, BigDecimal.valueOf(1000), BigDecimal.valueOf(250));

        // when
        ResponseEntity<BigDecimal> response = accountController.exchangePlnToUsd(accountId, request);

        // then
        assertEquals(BigDecimal.valueOf(250), response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).getUserByAccountId(accountId.toString());
        verify(exchangeRateService).convertPlnToUsd(BigDecimal.valueOf(1000));
        verify(userService).updateBalancesForPlnToUsd(accountId, BigDecimal.valueOf(1000), BigDecimal.valueOf(250));
    }

    @Test
    void testExchangeUsdToPln() {
        // given
        UUID accountId = UUID.randomUUID();
        ExchangeRequest request = new ExchangeRequest();
        request.setAmount(BigDecimal.valueOf(50));

        when(exchangeRateService.convertUsdToPln(BigDecimal.valueOf(50))).thenReturn(BigDecimal.valueOf(200));
        doNothing().when(userService).updateBalancesForUsdToPln(accountId, BigDecimal.valueOf(50), BigDecimal.valueOf(200));

        // when
        ResponseEntity<BigDecimal> response = accountController.exchangeUsdToPln(accountId, request);

        // then
        assertEquals(BigDecimal.valueOf(200), response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).getUserByAccountId(accountId.toString());
        verify(exchangeRateService).convertUsdToPln(BigDecimal.valueOf(50));
        verify(userService).updateBalancesForUsdToPln(accountId, BigDecimal.valueOf(50), BigDecimal.valueOf(200));
    }

    @Test
    void testGetAccountDetails() {
        // given
        String accountId = "test-account-id";
        AccountDetailsResponse mockResponse = new AccountDetailsResponse(
                accountId,
                "John",
                "Doe",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(200),
                "john.doe@example.com"
        );

        when(userService.getAccountDetails(accountId)).thenReturn(mockResponse);

        // when
        ResponseEntity<AccountDetailsResponse> response = accountController.getAccountDetails(accountId);

        // then
        assertEquals(mockResponse, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).getAccountDetails(accountId);
    }
}