package com.trading.bybit_api.config.connection;

import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BybitApiConfig {

    @Bean
    public BybitApiMarketRestClient bybitApiMarketRestClient() {
        return BybitApiClientFactory.newInstance().newMarketDataRestClient();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
