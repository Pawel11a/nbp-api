package ozdoba.example.nbp_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import ozdoba.example.nbp_api.model.ExchangeRatesSeries;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = "nbp.api.url=http://mock-api-url.com")
class ExchangeRateServiceTest {

    private String nbpApiUrl;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        nbpApiUrl = "http://mock-api-url.com";
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateNotAvailable() {
        // Given
        when(restTemplate.getForEntity(nbpApiUrl, ExchangeRatesSeries.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // When
        Exception exception = assertThrows(IllegalStateException.class, () -> exchangeRateService.fetchExchangeRate());

        // Then
        assertThat(exception.getMessage()).isEqualTo("Failed to fetch exchange rates from NBP API");
        assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("Exchange rates not available.");
    }

    @Test
    void shouldThrowExceptionWhenPlnAmountIsInvalid() {
        // Given
        BigDecimal invalidAmount = BigDecimal.ZERO;

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> exchangeRateService.convertPlnToUsd(invalidAmount));
        assertThat(exception.getMessage()).isEqualTo("Amount must be greater than zero.");
    }

    @Test
    void shouldThrowExceptionWhenUsdAmountIsInvalid() {
        // Given
        BigDecimal invalidAmount = BigDecimal.valueOf(-1);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> exchangeRateService.convertUsdToPln(invalidAmount));
        assertThat(exception.getMessage()).isEqualTo("Amount must be greater than zero.");
    }
}