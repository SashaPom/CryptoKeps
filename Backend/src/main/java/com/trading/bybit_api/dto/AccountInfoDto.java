package com.trading.bybit_api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class AccountInfoDto {
    String keyName;
    String apiKey;
    String apiSecret;
    boolean isConnected;
    // Карта балансов, где ключ – символ монеты, а значение – баланс для этой монеты
    List<BalanceDto> balances;
}
