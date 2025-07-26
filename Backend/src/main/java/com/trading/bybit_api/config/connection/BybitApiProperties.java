package com.trading.bybit_api.config.connection;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bybit.api")
public class BybitApiProperties {
    private boolean useDemo;
}

