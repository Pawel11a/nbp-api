package ozdoba.example.nbp_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ozdoba.example.nbp_api.model.ExchangeRate;
import ozdoba.example.nbp_api.model.ExchangeRatesSeries;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService implements IExchangeRateService {

    private final RestTemplate restTemplate;

    @Value("${nbp.api.url}")
    private String nbpApiUrl;

    public ExchangeRate fetchExchangeRate() {
        log.info("Fetching exchange rate from NBP API: {}", nbpApiUrl);
        try {
            ResponseEntity<ExchangeRatesSeries> response = restTemplate.getForEntity(nbpApiUrl, ExchangeRatesSeries.class);

            if (response == null) {
                log.error("Failed to fetch exchange rates. Response is null.");
                throw new IllegalArgumentException("Exchange rates not available.");
            }

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().getRates() == null || response.getBody().getRates().isEmpty()) {
                log.error("Failed to fetch exchange rates. HTTP status: {}, Body: {}",
                        response.getStatusCode(),
                        response.getBody());
                throw new IllegalArgumentException("Exchange rates not available.");
            }

            ExchangeRatesSeries.Rate rate = response.getBody().getRates().get(0);
            log.info("Fetched exchange rate: bid = {}, ask = {}", rate.getBid(), rate.getAsk());
            return new ExchangeRate(rate.getBid(), rate.getAsk());
        } catch (Exception e) {
            log.error("Error fetching exchange rate from NBP API", e);
            throw new IllegalStateException("Failed to fetch exchange rates from NBP API", e);
        }
    }

    public BigDecimal convertPlnToUsd(BigDecimal amountPln) {
        validateAmount(amountPln);
        log.info("Converting PLN to USD. Amount: {}", amountPln);

        ExchangeRate exchangeRate = fetchExchangeRate();
        BigDecimal result = amountPln.divide(exchangeRate.getAsk(), 2, HALF_UP);

        log.info("Converted {} PLN to {} USD using ask rate: {}", amountPln, result, exchangeRate.getAsk());
        return result;
    }

    public BigDecimal convertUsdToPln(BigDecimal amountUsd) {
        validateAmount(amountUsd);
        log.info("Converting USD to PLN. Amount: {}", amountUsd);

        ExchangeRate exchangeRate = fetchExchangeRate();
        BigDecimal result = amountUsd.multiply(exchangeRate.getBid()).setScale(2, HALF_UP);

        log.info("Converted {} USD to {} PLN using bid rate: {}", amountUsd, result, exchangeRate.getBid());
        return result;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}