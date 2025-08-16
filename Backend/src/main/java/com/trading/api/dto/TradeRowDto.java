package com.trading.api.dto;

import com.bybit.api.client.domain.trade.Side;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class TradeRowDto {
    String id;          // UUID as string
    Instant time;
    String symbol;
    Side side;        // BUY | SELL
    double qty;
    Double entry;
    Double exit;
    Double pnl;
    Double rr;          // приблизительно из TP/SL (см. маппер)
    Integer durationSec;
}
