Project Documentation
======================

*** Module 1: Bybit API ***

1. **com.trading.bybit_api.service.impl.BybitBalanceServiceImpl**
    - **Fields:**
        - `BybitApiAccountRestClient getRestClient(String apiKey, String apiSecret)`: Creates and returns a Bybit API account REST client using the provided API key and secret.
        - `BigDecimal SAFETY_MARGIN`: A constant representing a 1% safety margin.

    - **Methods:**
        - `List<BalanceDto> fetchBalances(String apiKey, String apiSecret)`: Fetches the current account balances from Bybit for the given API key and secret. Returns a list of `BalanceDto` objects.
        - `BigDecimal getTotalAvailableBalance(String coin, String apiKey, String apiSecret)`: Retrieves the total available balance for a specific coin using the provided API key and secret. Returns the available balance as a `BigDecimal`.
        - `boolean hasEnoughBalance(String coin, BigDecimal requiredAmount, String apiKey, String apiSecret)`: Checks if there is enough balance for a specific coin to meet the required amount. Returns a boolean indicating the result.
        - `BigDecimal getTotalEquity(String apiKey, String apiSecret)`: Calculates the total equity of the account in USD using the provided API key and secret. Returns the total equity as a `BigDecimal`.
        - `BigDecimal getUnrealizedPnl(String coin, String apiKey, String apiSecret)`: Retrieves the unrealized profit and loss (PnL) for a specific coin using the provided API key and secret. Returns the unrealized PnL as a `BigDecimal`.
        - `BalanceDto extractBalanceFromResponse(Map<String, Object> response, String targetCoin)`: Extracts balance information for a specific coin from the API response. Returns a `BalanceDto` object.

2. **com.trading.bybit_api.service.IBybitBalanceService**
    - **Methods:**
        - `List<BalanceDto> fetchBalances(String apiKey, String apiSecret)`: Fetches the current account balances from Bybit for the given API key and secret. Returns a list of `BalanceDto` objects.
        - `BigDecimal getTotalAvailableBalance(String coin, String apiKey, String apiSecret)`: Retrieves the total available balance for a specific coin using the provided API key and secret. Returns the available balance as a `BigDecimal`.
        - `boolean hasEnoughBalance(String coin, BigDecimal requiredAmount, String apiKey, String apiSecret)`: Checks if there is enough balance for a specific coin to meet the required amount. Returns a boolean indicating the result.
        - `BigDecimal getTotalEquity(String apiKey, String apiSecret)`: Calculates the total equity of the account in USD using the provided API key and secret. Returns the total equity as a `BigDecimal`.

3. **com.trading.bybit_api.dto.BalanceDto**
    - **Fields:**
        - `String symbol`: The name of the coin, e.g., USDT.
        - `BigDecimal availableBalance`: The available balance of the coin.
        - `BigDecimal totalBalance`: The total balance of the coin.
        - `BigDecimal unrealizedPnl`: The unrealized profit and loss of the coin.

4. **com.trading.bybit_api.config.connection.BybitAccountConnector**
    - **Fields:**
        - `BybitApiAccountRestClient apiRestClient`: The Bybit API account REST client.
        - `BybitApiTradeRestClient tradeRestClient`: The Bybit API trade REST client.
        - `BybitApiMarketRestClient marketRestClient`: The Bybit API market REST client.
        - `BybitApiPositionRestClient positionRestClient`: The Bybit API position REST client.

    - **Constructor:**
        - `BybitAccountConnector(String apiKey, String apiSecret)`: Initializes the connector with the provided API key and secret. Creates instances of the REST clients.

5. **com.trading.bybit_api.controller.AccountInfoController**
    - **Fields:**
        - `IBybitAccountInfoService accountInfoService`: The service for handling account information.

    - **Methods:**
        - `AccountInfoDto getAccountInfo(@PathVariable Long id)`: Retrieves account information by ID. Returns an `AccountInfoDto` object.
        - `AccountInfoDto createAccountInfo(@RequestBody AccountInfo accountInfo)`: Creates a new account information entry. Returns an `AccountInfoDto` object.
        - `AccountInfoDto updateAccountInfo(@PathVariable Long id, @RequestBody AccountInfo accountInfo)`: Updates existing account information by ID. Returns an `AccountInfoDto` object.
        - `void deleteAccountInfo(@PathVariable Long id)`: Deletes account information by ID.

6. **com.trading.bybit_api.repository.AccountInfoRepository**
    - **Methods:**
        - `Optional<AccountInfo> findById(Long id)`: Finds an account information entry by its ID. Returns an `Optional<AccountInfo>`.
        - `AccountInfo save(AccountInfo accountInfo)`: Saves an account information entry. Returns the saved `AccountInfo`.
        - `void deleteById(Long id)`: Deletes an account information entry by its ID.

7. **com.trading.bybit_api.domain.AccountInfo**
    - **Fields:**
        - `Long id`: The unique identifier of the account information.
        - `String apiKey`: The API key associated with the account.
        - `String apiSecret`: The API secret associated with the account.
        - `String accountName`: The name of the account.

8. **com.trading.bybit_api.dto.AccountInfoDto**
    - **Fields:**
        - `Long id`: The unique identifier of the account information.
        - `String apiKey`: The API key associated with the account.
        - `String apiSecret`: The API secret associated with the account.
        - `String accountName`: The name of the account.