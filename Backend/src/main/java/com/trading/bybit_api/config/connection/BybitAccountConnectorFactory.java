package com.trading.bybit_api.config.connection;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BybitAccountConnectorFactory {
    public BybitAccountConnector createConnector(String apiKey, String apiSecret, boolean useDemo) {
        return new BybitAccountConnector(apiKey, apiSecret, useDemo);
    }
}
