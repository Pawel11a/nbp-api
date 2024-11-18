package ozdoba.example.nbp_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDetailsResponse {
    private String accountId;
    private String firstName;
    private String lastName;
    private BigDecimal initialBalancePLN;
    private BigDecimal initialBalanceUSD;
    private String username;
}

