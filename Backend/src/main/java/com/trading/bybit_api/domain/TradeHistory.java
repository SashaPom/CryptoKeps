package com.trading.bybit_api.domain;

import com.bybit.api.client.domain.trade.Side;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class TradeHistory {
    UUID id;
    UUID accountId;
    String symbol;
    Side side;
    BigDecimal quantity;
    BigDecimal entryPrice;
    BigDecimal stopLoss;
    BigDecimal takeProfit;
    LocalDateTime timestamp;
}
