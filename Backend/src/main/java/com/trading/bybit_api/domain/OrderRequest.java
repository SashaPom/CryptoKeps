package com.trading.bybit_api.domain;

import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.trade.Side;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class OrderRequest {
    UUID orderId; // Уникальный идентификатор ордера на бирже
    String symbol; // Торговая пара, например, BTC/USDT
    TradeOrderType type; // Тип ордера (Market, Limit)
    Side side; // Направление сделки (Buy/Sell)
    BigDecimal quantity; // Количество актива
    BigDecimal price; // Цена (для лимитных ордеров)
    BigDecimal takeProfit;
    BigDecimal stopLoss;
    String status; // Статус ордера (Pending, Filled, Cancelled)
}
