package com.trading.bybit_api.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class OrderResponse {
    UUID orderId; // Уникальный идентификатор ордера
    String symbol; // Торговая пара
    String status; // Статус ордера (например, Filled, Cancelled)
}
