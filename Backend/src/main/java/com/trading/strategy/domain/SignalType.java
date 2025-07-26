package com.trading.strategy.domain;

/**
 * Типы сигналов:
 * - LONG: сигнал на открытие лонг-позиции
 * - CLOSE: сигнал на закрытие позиции (вместо шорта)
 * - NO_SIGNAL: нет сигнала
 */
public enum SignalType {
    LONG,
    CLOSE,
    NO_SIGNAL
}
