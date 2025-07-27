package com.trading.bybit_api.config.connection;

import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.restApi.BybitApiPositionRestClient;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.trading.common.BybitEndpoints;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class BybitAccountConnector {
    private final BybitApiAccountRestClient apiRestClient;
    private final BybitApiTradeRestClient tradeRestClient;
    private final BybitApiMarketRestClient marketRestClient;
    private final BybitApiPositionRestClient positionRestClient;

    public BybitAccountConnector(String apiKey, String apiSecret, boolean useDemo) {
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            this.apiRestClient = null;
            this.tradeRestClient = null;
            this.marketRestClient = null;
            this.positionRestClient = null;
            log.error("Bybit API key/secret is not configured. Please set BYBIT_API_KEY and BYBIT_API_SECRET as environment variables.");
        } else {
            String domain = useDemo ? BybitEndpoints.BYBIT_DEMO_TRADE_DOMAIN : BybitEndpoints.BYBIT_PROD_TRADE_DOMAIN;
            var factory = BybitApiClientFactory.newInstance(apiKey, apiSecret, domain);
            this.apiRestClient = factory.newAccountRestClient();
            this.tradeRestClient = factory.newTradeRestClient();
            this.marketRestClient = factory.newMarketDataRestClient();
            this.positionRestClient = factory.newPositionRestClient();
        }
    }
}
