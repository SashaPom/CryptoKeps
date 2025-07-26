package com.trading.bybit_api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class AccountInfo {

    @NotBlank(message = "Key name must not be blank")
    String keyName; // Уникальное имя ключа для идентификации аккаунта

    @NotBlank(message = "API key must not be blank")
    String apiKey; // API-ключ пользователя

    @NotBlank(message = "API secret must not be blank")
    String apiSecret; // Секретный ключ пользователя

    @NotNull(message = "Balances map must not be null")
    List<Balance> balances; // Балансы для каждой монеты (например, {"USDT": Balance})

    @JsonProperty("connected")
    boolean isConnected; // Статус подключения к аккаунту
}
