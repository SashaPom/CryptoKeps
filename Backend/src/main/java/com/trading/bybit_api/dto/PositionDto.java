package com.trading.bybit_api.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class PositionDto {
    String symbol; // Торговая пара, например, BTC/USDT
    BigDecimal positionValue; // Размер позиции
    BigDecimal entryPrice; // Цена входа в позицию
    BigDecimal currentPrice;
    BigDecimal unrealizedPnl; // Нереализованная прибыль или убыток
    BigDecimal takeProfit;
    BigDecimal stopLoss;
    BigDecimal leverage; // Используемое плечо
}
