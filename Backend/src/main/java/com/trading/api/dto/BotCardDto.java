package com.trading.api.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class BotCardDto {
    String id;                 // UUID как строка (для фронта)
    String strategyName;       // пока используем keyName, можно поменять позже
    String exchange;           // "Bybit"
    String accountKeyName;     // keyName из account_info
    String status;             // RUNNING | PAUSED | ERROR
    Instant lastSync;          // последний trade.timestamp или now
    BotKpiDto kpis;
    List<EquityPointDto> equitySeries;
}
