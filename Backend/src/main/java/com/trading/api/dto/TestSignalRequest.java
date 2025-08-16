package com.trading.api.dto;

import lombok.Data;

@Data
public class TestSignalRequest {
    private String symbol;
    private String side;        // BUY | SELL
    private String orderType;   // MARKET | LIMIT
    private double quantity;
    private Double price;
    private Double takeProfit;
    private Double stopLoss;
    private Double atr;
}
