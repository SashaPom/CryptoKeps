package com.trading.bybit_api.service.impl;

import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.OrderRequestDto;
import com.trading.bybit_api.entity.AccountInfoEntity;
import com.trading.bybit_api.entity.TradeHistoryEntity;
import com.trading.bybit_api.exception.AccountInfoNotFoundException;
import com.trading.bybit_api.repository.AccountInfoRepository;
import com.trading.bybit_api.repository.TradeHistoryRepository;
import com.trading.bybit_api.service.interfaces.ITradeHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeHistoryServiceImpl implements ITradeHistoryService {
    private final TradeHistoryRepository tradeHistoryRepository;
    private final AccountInfoRepository accountInfoRepository;

    @Override
    public void saveTrade(OrderRequestDto orderRequest, AccountInfoDto accountInfo) {
        AccountInfoEntity accountInfoEntity = accountInfoRepository.findByApiKey(accountInfo.getApiKey())
                .orElseThrow(() -> new AccountInfoNotFoundException("Account not found for API keyName: " + accountInfo.getKeyName()));

        TradeHistoryEntity tradeHistory = TradeHistoryEntity.builder()
                .account(accountInfoEntity)
                .symbol(orderRequest.getSymbol())
                .side(orderRequest.getSide())
                .quantity(orderRequest.getQuantity())
                .entryPrice(orderRequest.getPrice())
                .stopLoss(orderRequest.getStopLoss())
                .takeProfit(orderRequest.getTakeProfit())
                .timestamp(ZonedDateTime.now(ZoneId.of("Europe/Kyiv")).toLocalDateTime())
                .build();

        tradeHistoryRepository.save(tradeHistory);
        log.info("Trade saved: {}", tradeHistory);
    }
}
