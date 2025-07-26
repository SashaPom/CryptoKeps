package com.trading.bybit_api.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ApiError {
    int code; // Код ошибки API
    String message; // Сообщение об ошибке
    String details; // Дополнительная информация (если есть)
}
