package ozdoba.example.nbp_api.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExchangeRatesSeries {
    private List<Rate> rates;

    @Data
    public static class Rate {
        private BigDecimal bid;
        private BigDecimal ask;
    }
}

