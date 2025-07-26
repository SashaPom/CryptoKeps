package com.trading.bybit_api.dto;

import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.trade.Side;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class OrderRequestDto {
    UUID orderId; // Уникальный идентификатор ордера на бирже
    String symbol; // Торговая пара, например, BTC/USDT
    TradeOrderType type; // Тип ордера (Market, Limit)
    Side side; // Направление сделки (Buy/Sell)
    BigDecimal quantity; // Количество актива
    BigDecimal price; // Цена (для лимитных ордеров)
    BigDecimal takeProfit;
    BigDecimal stopLoss;
    BigDecimal leverage;
    String status; // Статус ордера (Pending, Filled, Cancelled)
}
