package com.trading.strategy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signal {
    private String symbol;
    private String interval;
    private String action;   // BUY, SELL, HOLD
    private double macd;
    private double signalLine;
    private double histogram;
    private long timestamp;
}
