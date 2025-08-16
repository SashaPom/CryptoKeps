package com.trading.api.mapper;

import com.trading.api.dto.TradeRowDto;
import com.trading.bybit_api.entity.TradeHistoryEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class ApiMappers {

    private ApiMappers() {}

    public static TradeRowDto toTradeRowDto(TradeHistoryEntity e) {
        Double rr = null;
        // Приблизительный RR, если есть TP/SL/Entry
        if (e.getTakeProfit() != null && e.getStopLoss() != null && e.getEntryPrice() != null) {
            double tp = e.getTakeProfit().doubleValue();
            double sl = e.getStopLoss().doubleValue();
            double en = e.getEntryPrice().doubleValue();
            double risk = Math.abs(en - sl);
            double reward = Math.abs(tp - en);
            if (risk > 0) rr = reward / risk;
        }

        Integer duration = null; // Если появятся open/close time — посчитаем

        Instant ts = e.getTimestamp().atOffset(ZoneOffset.UTC).toInstant();

        return TradeRowDto.builder()
                .id(e.getId().toString())
                .time(ts)
                .symbol(e.getSymbol())
                .side(e.getSide())
                .qty(toD(e.getQuantity()))
                .entry(toD(e.getEntryPrice()))
                .exit(toD(e.getClosePrice()))
                .pnl(toD(e.getPnl()))
                .rr(rr)
                .durationSec(duration)
                .build();
    }

    private static double toD(BigDecimal b) { return b == null ? 0d : b.doubleValue(); }
}
