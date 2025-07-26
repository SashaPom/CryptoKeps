package com.trading.strategy.config;

import com.trading.strategy.domain.KlineInterval;
import com.trading.strategy.domain.TimePeriod;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * Класс для конфигурации стратегии.
 * Здесь можно хранить настройки анализа (интервал, период и т.д.).
 */
@Data
@Configuration
public class AnalysisConfig {
    private KlineInterval interval = KlineInterval.ONE_MINUTE;
    private TimePeriod period = new TimePeriod(7); // Например, 7 дней
    private int periodMinutes = 5;
}