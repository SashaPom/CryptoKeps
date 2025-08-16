package com.trading.api.service;

import com.trading.api.dto.*;
import com.trading.bybit_api.entity.AccountInfoEntity;
import com.trading.bybit_api.entity.TradeHistoryEntity;
import com.trading.bybit_api.repository.AccountInfoRepository;
import com.trading.bybit_api.repository.TradeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BotQueryService {

    private final AccountInfoRepository accountRepo;
    private final TradeHistoryRepository tradeRepo;

    public List<BotCardDto> listBots(String q, String status, String period) {
        // 1) берём все аккаунты (пока 1 аккаунт = 1 бот)
        List<AccountInfoEntity> accounts = accountRepo.findAll();

        // фильтр по q (по keyName и apiKey)
        if (q != null && !q.isBlank()) {
            String low = q.toLowerCase();
            accounts = accounts.stream()
                    .filter(a -> (a.getKeyName() != null && a.getKeyName().toLowerCase().contains(low))
                            || (a.getApiKey() != null && a.getApiKey().toLowerCase().contains(low)))
                    .collect(Collectors.toList());
        }

        Instant from = fromByPeriod(period);

        List<BotCardDto> result = new ArrayList<>();
        for (AccountInfoEntity acc : accounts) {
            // 2) выбираем сделки за период
            LocalDateTime fromLdt = LocalDateTime.ofInstant(from, ZoneOffset.UTC);

            List<TradeHistoryEntity> trades = tradeRepo.findByAccountIdAndTimestampAfterOrderByTimestampAsc(
                    acc.getId(), fromLdt
            );

            // KPI
            double pnlAbs = trades.stream().map(TradeHistoryEntity::getPnl).filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue).sum();
            int tradesCount = trades.size();
            long wins = trades.stream().map(TradeHistoryEntity::getPnl).filter(Objects::nonNull)
                    .filter(p -> p.doubleValue() > 0).count();
            double winRate = tradesCount == 0 ? 0 : (wins * 100.0 / tradesCount);

            // Примерная equitySeries: базовая equity 10000 + накопленная pnl
            double base = 10_000d;
            double running = base;
            List<EquityPointDto> series = new ArrayList<>();
            for (TradeHistoryEntity t : trades) {
                if (t.getPnl() != null) running += t.getPnl().doubleValue();
                series.add(EquityPointDto.builder()
                        .t(t.getTimestamp().atOffset(ZoneOffset.UTC).toInstant())
                        .equity(running)
                        .build());
            }
            // max drawdown (%)
            double maxEquity = base, maxDD = 0;
            for (EquityPointDto p : series) {
                maxEquity = Math.max(maxEquity, p.getEquity());
                double dd = maxEquity == 0 ? 0 : (maxEquity - p.getEquity()) / maxEquity * 100.0;
                maxDD = Math.max(maxDD, dd);
            }

            // avgRR по формуле из маппера
            List<Double> rrs = trades.stream().map(t -> {
                if (t.getTakeProfit() == null || t.getStopLoss() == null || t.getEntryPrice() == null) return null;
                double tp = t.getTakeProfit().doubleValue();
                double sl = t.getStopLoss().doubleValue();
                double en = t.getEntryPrice().doubleValue();
                double risk = Math.abs(en - sl);
                double reward = Math.abs(tp - en);
                return risk > 0 ? reward / risk : null;
            }).filter(Objects::nonNull).toList();
            double avgRR = rrs.isEmpty() ? 0 : rrs.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            Instant lastSync = trades.isEmpty() ? Instant.now() : trades.get(trades.size() - 1).getTimestamp().atOffset(ZoneOffset.UTC).toInstant();

            String derivedStatus = acc.isConnected() ? "RUNNING" : "PAUSED";
            if (status != null && !"ALL".equalsIgnoreCase(status)) {
                if (!derivedStatus.equalsIgnoreCase(status)) continue; // фильтр статуса
            }

            result.add(BotCardDto.builder()
                    .id(acc.getId().toString())
                    .strategyName(acc.getKeyName())     // пока так; позже появится отдельная таблица стратегий
                    .exchange("Bybit")
                    .accountKeyName(acc.getKeyName())
                    .status(derivedStatus)
                    .lastSync(lastSync)
                    .kpis(BotKpiDto.builder()
                            .pnlAbs(round2(pnlAbs))
                            .pnlPct(round2(base == 0 ? 0 : pnlAbs / base * 100.0))
                            .trades(tradesCount)
                            .winRate(round2(winRate))
                            .maxDrawdownPct(round2(maxDD))
                            .avgRR(round2(avgRR))
                            .build())
                    .equitySeries(series)
                    .build());
        }

        return result;
    }

    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }

    private static Instant fromByPeriod(String p) {
        if ("7d".equalsIgnoreCase(p))  return Instant.now().minus(7, ChronoUnit.DAYS);
        if ("30d".equalsIgnoreCase(p)) return Instant.now().minus(30, ChronoUnit.DAYS);
        // default 24h
        return Instant.now().minus(24, ChronoUnit.HOURS);
    }
}
