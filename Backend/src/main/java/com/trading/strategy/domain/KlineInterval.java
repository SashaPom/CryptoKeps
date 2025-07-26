package com.trading.strategy.domain;

import lombok.*;

/**
 * Перечисление таймфреймов (интервалов) для свечей (K-line).
 */

@Getter
@ToString
@RequiredArgsConstructor
public enum KlineInterval {
    ONE_MINUTE("1m"),
    THREE_MINUTES("3m"),
    FIVE_MINUTES("5m"),
    FIFTEEN_MINUTES("15m"),
    THIRTY_MINUTES("30m"),
    ONE_HOUR("1h"),
    FOUR_HOURS("4h"),
    ONE_DAY("1d");

    private final String code;
}
