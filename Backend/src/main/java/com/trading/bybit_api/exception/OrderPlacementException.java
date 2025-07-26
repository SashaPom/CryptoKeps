package com.trading.bybit_api.exception;

public class OrderPlacementException extends RuntimeException {
    public OrderPlacementException(String message) {
        super(message);
    }
}
