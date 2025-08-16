package com.trading.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BotKpiDto {
    double pnlAbs;
    double pnlPct;
    int trades;
    double winRate;
    double maxDrawdownPct;
    double avgRR;
}
