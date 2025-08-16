package com.trading.api.service;

import com.trading.api.dto.TradeRowDto;
import com.trading.api.mapper.ApiMappers;
import com.trading.bybit_api.repository.TradeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeQueryService {

    private final TradeHistoryRepository tradeRepo;

    public List<TradeRowDto> recentTrades(String botId, String period) {
        Instant from = switch (period) {
            case "7d" -> Instant.now().minus(7, ChronoUnit.DAYS);
            case "30d" -> Instant.now().minus(30, ChronoUnit.DAYS);
            default -> Instant.now().minus(24, ChronoUnit.HOURS);
        };
        LocalDateTime fromLdt = LocalDateTime.ofInstant(from, ZoneOffset.UTC);

        var list = tradeRepo.findByAccountIdAndTimestampAfterOrderByTimestampAsc(
                java.util.UUID.fromString(botId),
                fromLdt
        );
        return list.stream().map(ApiMappers::toTradeRowDto).toList();
    }
}
