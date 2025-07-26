package com.trading.bybit_api.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class BalanceDto {
    String symbol;                // Название монеты, например, USDT
    BigDecimal availableBalance;  // Доступный баланс
    BigDecimal totalBalance;      // Общий баланс
}
