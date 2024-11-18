# NBP API Application

This Spring Boot application provides a secure API for currency exchange (PLN to USD and USD to PLN) and account management. It includes user authentication, account details retrieval, and balance updates based on exchange rates.

## Features

- **User Registration**: Register users with initial balances and roles.
- **Account Details**: Fetch account details by ID.
- **Currency Exchange**: Convert balances between PLN and USD.
- **Secure Endpoints**: Secure access using basic authentication and roles.
- **Integrated NBP API**: Fetch live exchange rates from NBP API.

## Endpoints

### User Endpoints

1. **Register a new user**
   POST /api/users
   **Request**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "username": "john.doe@example.com",
  "password": "user",
  "role": "ROLE_USER",
  "initialBalancePLN": 1000
}
```

Response:

HTTP 201 Created
User registered successfully


Fetch account details

GET /api/accounts/{accountId}

Authorization: Basic Auth required.

Response:
```json
{
"accountId": "c24bf5bd-57fc-4e64-918a-1d6bc75de18a",
"firstName": "John",
"lastName": "Doe",
"initialBalancePLN": 1000.00,
"initialBalanceUSD": 250.00,
"username": "john.doe@example.com"
}
```

Exchange Endpoints
Exchange PLN to USD

POST /api/accounts/{accountId}/pln-to-usd

```json
{
"amount": 1000
}

250.00
```

Exchange USD to PLN

POST /api/accounts/{accountId}/usd-to-pln

Request:
```json
{
"amount": 100
}
```

Response:
400.00

## API Documentation

The API documentation is available through Swagger. Use the following links to access it:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html#/): Interactive API documentation.
- [OpenAPI JSON Spec](http://localhost:8080/v3/api-docs): Raw OpenAPI specification in JSON format.

Make sure the application is running locally on port `8080` to access the documentation.
