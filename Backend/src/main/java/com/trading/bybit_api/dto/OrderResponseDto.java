package com.trading.bybit_api.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class OrderResponseDto {
    UUID orderId; // Уникальный идентификатор ордера
    String status; // Статус ордера (например, Filled, Cancelled)
//    String symbol; // Торговая пара
//    BigDecimal executedQuantity; // Исполненное количество
//    BigDecimal averagePrice; // Средняя цена исполнения
}
