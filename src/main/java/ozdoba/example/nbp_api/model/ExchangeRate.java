package ozdoba.example.nbp_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ExchangeRate {
    private final BigDecimal bid;
    private final BigDecimal ask;
}