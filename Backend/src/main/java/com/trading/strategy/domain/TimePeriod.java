package com.trading.strategy.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Пример класса, описывающего период времени (например, 7 дней).
 */
@Getter
@RequiredArgsConstructor
public class TimePeriod {
    private final int days;

}
