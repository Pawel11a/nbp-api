package ozdoba.example.nbp_api.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ozdoba.example.nbp_api.model.AccountDetailsResponse;
import ozdoba.example.nbp_api.model.AppUser;
import ozdoba.example.nbp_api.repository.UserRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addUser(AppUser appUser) {
        log.info("Attempting to add user with username: {}", appUser.getUsername());
        validateNewUser(appUser);
        appUser.setAccountId(UUID.randomUUID().toString());
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        userRepository.save(appUser);
        log.info("User added successfully with accountId: {}", appUser.getAccountId());
    }

    public AccountDetailsResponse getAccountDetails(String accountId) {
        log.info("Fetching account details for accountId: {}", accountId);
        AppUser user = findAccountById(accountId);
        log.info("Fetched account details for user: {}", user.getUsername());
        return mapToAccountDetailsResponse(user);
    }

    public AppUser getUserByAccountId(String id) {
        log.info("Fetching user by accountId: {}", id);
        return findAccountById(id);
    }

    public void updateBalancesForPlnToUsd(UUID accountId, BigDecimal amountPln, BigDecimal amountUsd) {
        log.info("Updating balances for accountId: {} (PLN to USD). Amount PLN: {}, Amount USD: {}", accountId, amountPln, amountUsd);
        AppUser account = findAccountById(accountId.toString());
        validateBalance(account.getInitialBalancePLN(), amountPln, "PLN", accountId);
        updateAccountBalances(account, account.getInitialBalancePLN().subtract(amountPln), account.getInitialBalanceUSD().add(amountUsd));
    }

    public void updateBalancesForUsdToPln(UUID accountId, BigDecimal amountUsd, BigDecimal amountPln) {
        log.info("Updating balances for accountId: {} (USD to PLN). Amount USD: {}, Amount PLN: {}", accountId, amountUsd, amountPln);
        AppUser account = findAccountById(accountId.toString());
        validateBalance(account.getInitialBalanceUSD(), amountUsd, "USD", accountId);
        updateAccountBalances(account, account.getInitialBalancePLN().add(amountPln), account.getInitialBalanceUSD().subtract(amountUsd));
    }

    private void validateNewUser(AppUser appUser) {
        if (userRepository.existsByUsername(appUser.getUsername())) {
            log.warn("Username {} is already in use", appUser.getUsername());
            throw new IllegalArgumentException("Username is already in use");
        }
    }

    private AppUser findAccountById(String accountId) {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found for accountId: {}", accountId);
                    return new IllegalArgumentException("Account not found");
                });
    }

    private void validateBalance(BigDecimal currentBalance, BigDecimal requestedAmount, String currency, UUID accountId) {
        if (currentBalance.compareTo(requestedAmount) < 0) {
            log.warn("Insufficient {} balance for accountId: {}. Current balance: {}, Requested: {}", currency, accountId, currentBalance, requestedAmount);
            throw new IllegalArgumentException("Insufficient balance in " + currency);
        }
    }

    private void updateAccountBalances(AppUser account, BigDecimal newPlnBalance, BigDecimal newUsdBalance) {
        account.setInitialBalancePLN(newPlnBalance);
        account.setInitialBalanceUSD(newUsdBalance);
        userRepository.save(account);
        log.info("Balances updated successfully for accountId: {}", account.getAccountId());
    }

    private AccountDetailsResponse mapToAccountDetailsResponse(AppUser user) {
        return new AccountDetailsResponse(
                user.getAccountId(),
                user.getFirstName(),
                user.getLastName(),
                user.getInitialBalancePLN(),
                user.getInitialBalanceUSD(),
                user.getUsername()
        );
    }
}