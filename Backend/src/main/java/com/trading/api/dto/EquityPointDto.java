package com.trading.api.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class EquityPointDto {
    Instant t;
    double equity;
}
