package com.trading.strategy.dto;

import com.trading.strategy.domain.SignalType;
import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Value
@Builder(toBuilder = true)
public class SignalDTO {
    String symbol;
    SignalType signalType;
    BigDecimal entryPrice;
    ZonedDateTime timestamp;
    String strategyName;
    String timeframe;
}
