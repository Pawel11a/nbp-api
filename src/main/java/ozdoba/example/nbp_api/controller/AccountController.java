package ozdoba.example.nbp_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ozdoba.example.nbp_api.model.AccountDetailsResponse;
import ozdoba.example.nbp_api.model.ExchangeRequest;
import ozdoba.example.nbp_api.service.ExchangeRateService;
import ozdoba.example.nbp_api.service.UserService;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Account API", description = "Endpoints for managing accounts and currency exchange.")
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final ExchangeRateService exchangeRateService;
    private final UserService userService;

    @Operation(summary = "Exchange PLN to USD", description = "Converts PLN to USD for a given account.")
    @PostMapping("/{accountId}/pln-to-usd")
    public ResponseEntity<BigDecimal> exchangePlnToUsd(
            @PathVariable UUID accountId,
            @Valid @RequestBody ExchangeRequest request) {
        log.info("Request to exchange PLN to USD: accountId={}, amountPln={}", accountId, request.getAmount());
        userService.getUserByAccountId(accountId.toString());
        BigDecimal amountUsd = exchangeRateService.convertPlnToUsd(request.getAmount());
        userService.updateBalancesForPlnToUsd(accountId, request.getAmount(), amountUsd);
        log.info("Exchange completed: accountId={}, amountUsd={}", accountId, amountUsd);
        return ResponseEntity.ok(amountUsd);
    }

    @Operation(summary = "Exchange USD to PLN", description = "Converts USD to PLN for a given account.")
    @PostMapping("/{accountId}/usd-to-pln")
    public ResponseEntity<BigDecimal> exchangeUsdToPln(
            @PathVariable UUID accountId,
            @Valid @RequestBody ExchangeRequest request) {
        log.info("Request to exchange USD to PLN: accountId={}, amountPln={}", accountId, request.getAmount());
        userService.getUserByAccountId(accountId.toString());
        BigDecimal amountPln = exchangeRateService.convertUsdToPln(request.getAmount());
        userService.updateBalancesForUsdToPln(accountId, request.getAmount(), amountPln);
        log.info("Exchange completed: accountId={}, amountUsd={}", accountId, amountPln);
        return ResponseEntity.ok(amountPln);
    }

    @Operation(summary = "Get account details", description = "Retrieves details for a specific account.")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDetailsResponse> getAccountDetails(@PathVariable String accountId) {
        AccountDetailsResponse response = userService.getAccountDetails(accountId);
        return ResponseEntity.ok(response);
    }
}

