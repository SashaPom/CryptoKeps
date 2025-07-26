package com.trading.bybit_api.exception;

public class AccountInfoNotFoundException extends RuntimeException {
    public AccountInfoNotFoundException(String keyName) {
        super("Account info not found for key: " + keyName);
    }
}
