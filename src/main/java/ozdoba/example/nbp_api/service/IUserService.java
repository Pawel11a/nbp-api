package ozdoba.example.nbp_api.service;

import ozdoba.example.nbp_api.model.AccountDetailsResponse;
import ozdoba.example.nbp_api.model.AppUser;

import java.math.BigDecimal;
import java.util.UUID;

public interface IUserService {
    void addUser(AppUser appUser);
    AccountDetailsResponse getAccountDetails(String accountId);
    AppUser getUserByAccountId(String id);
    void updateBalancesForPlnToUsd(UUID accountId, BigDecimal amountPln, BigDecimal amountUsd);
    void updateBalancesForUsdToPln(UUID accountId, BigDecimal amountUsd, BigDecimal amountPln);
}
