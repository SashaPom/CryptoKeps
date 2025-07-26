package com.trading.bybit_api.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class Balance {
    String symbol; // Название монеты, например, USDT
    BigDecimal availableBalance; // Доступный баланс
    BigDecimal totalBalance; // Общий баланс
}
