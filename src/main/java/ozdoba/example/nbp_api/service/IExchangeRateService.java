package ozdoba.example.nbp_api.service;

import ozdoba.example.nbp_api.model.ExchangeRate;

import java.math.BigDecimal;

public interface IExchangeRateService {
    ExchangeRate fetchExchangeRate();
    BigDecimal convertPlnToUsd(BigDecimal amountPln);
    BigDecimal convertUsdToPln(BigDecimal amountUsd);
}
