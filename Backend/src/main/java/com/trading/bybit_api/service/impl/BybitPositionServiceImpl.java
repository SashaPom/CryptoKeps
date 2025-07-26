package com.trading.bybit_api.service.impl;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.restApi.BybitApiPositionRestClient;
import com.trading.bybit_api.config.connection.BybitAccountConnector;
import com.trading.bybit_api.config.connection.BybitAccountConnectorFactory;
import com.trading.bybit_api.config.connection.BybitApiProperties;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.OrderRequestDto;
import com.trading.bybit_api.dto.PositionDto;
import com.trading.bybit_api.entity.AccountInfoEntity;
import com.trading.bybit_api.entity.TradeHistoryEntity;
import com.trading.bybit_api.exception.AccountInfoNotFoundException;
import com.trading.bybit_api.exception.OrderNotFoundException;
import com.trading.bybit_api.repository.AccountInfoRepository;
import com.trading.bybit_api.repository.TradeHistoryRepository;
import com.trading.bybit_api.service.interfaces.IBybitOrderService;
import com.trading.bybit_api.service.interfaces.IBybitPositionService;
import com.trading.bybit_api.service.interfaces.ITradeHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.trading.bybit_api.service.impl.WebhookServiceImpl.LEVERAGE;


@Slf4j
@Service
@RequiredArgsConstructor
public class BybitPositionServiceImpl implements IBybitPositionService {
    private BybitApiProperties properties;
    private final IBybitOrderService orderService;
    private final ITradeHistoryService tradeHistoryService;
    private final TradeHistoryRepository tradeHistoryRepository;
    private final AccountInfoRepository accountInfoRepository;

    @Autowired
    public BybitPositionServiceImpl(BybitApiProperties properties, IBybitOrderService orderService, ITradeHistoryService tradeHistoryService, TradeHistoryRepository tradeHistoryRepository, AccountInfoRepository accountInfoRepository) {
        this.properties = properties;
        this.orderService = orderService;
        this.tradeHistoryService = tradeHistoryService;
        this.tradeHistoryRepository = tradeHistoryRepository;
        this.accountInfoRepository = accountInfoRepository;
    }

    private BybitApiPositionRestClient getRestClient(String apiKey, String apiSecret) {
        BybitAccountConnector connector = BybitAccountConnectorFactory.createConnector(apiKey, apiSecret, properties.isUseDemo());
        BybitApiPositionRestClient client = connector.getPositionRestClient();
        if (client == null) {
            log.error("Failed to create BybitApiPositionRestClient");
            throw new RuntimeException("Failed to create BybitApiPositionRestClient");
        }
        return client;
    }

    @Override
    public void openLongPosition(String symbol, AccountInfoDto accountInfo, BigDecimal entryPrice, BigDecimal quantity, BigDecimal stopLoss, BigDecimal takeProfitPrice) {
        log.info("Opening long position: {}, entryPrice: {}, quantity: {}, stopLoss: {}, leverage: {}", symbol, entryPrice, quantity, stopLoss, LEVERAGE);

        OrderRequestDto newOrder = OrderRequestDto.builder()
                .symbol(symbol)
                .side(Side.BUY)
                .type(TradeOrderType.MARKET)
                .price(entryPrice)
                .leverage(LEVERAGE)
                .quantity(quantity)
                .takeProfit(takeProfitPrice)
                .stopLoss(stopLoss)
                .build();

        orderService.placeOrder(newOrder, accountInfo);
        tradeHistoryService.saveTrade(newOrder, accountInfo); // Сохраняем историю сделки
        log.info("Long position placed successfully: {}", newOrder);
    }

    @Override
    public PositionDto getPositionInfo(String symbol, AccountInfoDto accountInfo) {
        BybitApiPositionRestClient positionRestClient = getRestClient(accountInfo.getApiKey(), accountInfo.getApiSecret());

        PositionDto positionDto = PositionDto.builder().build();

        try {
            PositionDataRequest positionRequest = PositionDataRequest.builder()
                    .category(CategoryType.LINEAR)
                    .symbol(symbol)
                    .build();

            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> positionRestClient.getPositionInfo(positionRequest));

            Object response = future.get(); // Можно использовать thenApply/thenAccept для дальнейшей асинхронной обработки

            if (response instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) response;
                if (responseMap.containsKey("result")) {
                    Map<String, Object> result = (Map<String, Object>) responseMap.get("result");
                    List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
                    if (list != null) {
                        for (Map<String, Object> positionInfo : list) {
                            String positionValue = (String) positionInfo.get("positionValue");
                            if (positionValue != null && !positionValue.isEmpty()) {
                                positionDto = positionDto.toBuilder()
                                        .symbol((String) positionInfo.get("symbol"))
                                        .positionValue(new BigDecimal((String) positionInfo.get("positionValue")))
                                        .entryPrice(new BigDecimal((String) positionInfo.get("avgPrice")))
                                        .currentPrice(new BigDecimal((String) positionInfo.get("markPrice")))
                                        .unrealizedPnl(new BigDecimal((String) positionInfo.get("unrealisedPnl")))
                                        .takeProfit((positionInfo.get("takeProfit") != null && !((String) positionInfo.get("takeProfit")).isEmpty()) ? new BigDecimal((String) positionInfo.get("takeProfit")) : null)
                                        .stopLoss((positionInfo.get("stopLoss") != null && !((String) positionInfo.get("stopLoss")).isEmpty()) ? new BigDecimal((String) positionInfo.get("stopLoss")) : null)
                                        .leverage(new BigDecimal((String) positionInfo.get("leverage")))
                                        .build();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception while fetching position info for symbol: {}", symbol, e);
        }

        return positionDto;
    }

    @Override
    public void closePosition(String symbol, AccountInfoDto accountInfo, BigDecimal quantity, BigDecimal closePrice) {
        log.info("Closing position: Selling {}", quantity);

        UUID accountInfoId = accountInfoRepository.findByApiKey(accountInfo.getApiKey())
                .map(AccountInfoEntity::getId)
                .orElseThrow(() -> new AccountInfoNotFoundException("Account not found for API keyName: " + accountInfo.getKeyName()));

        OrderRequestDto closeOrder = OrderRequestDto.builder()
                .symbol(symbol)
                .side(Side.SELL)
                .type(TradeOrderType.MARKET)
                .quantity(quantity)
                .build();

        orderService.placeOrder(closeOrder, accountInfo);
        log.info("Order placed to close position: {}", closeOrder);

        TradeHistoryEntity lastTrade = tradeHistoryRepository.findLastTrade(accountInfoId, symbol)
                .orElseThrow(() -> new OrderNotFoundException("No open trade found for symbol: " + symbol));
        if (lastTrade == null) {
            log.error("No open trade found for {} to calculate PnL", symbol);
            return;
        }

        BigDecimal pnl = calculatePnL(lastTrade.getSide(), lastTrade.getEntryPrice(), closePrice, quantity);

        lastTrade = lastTrade.toBuilder()
                .closePrice(closePrice)
                .pnl(pnl)
                .build();

        tradeHistoryRepository.save(lastTrade);
        log.info("Position closed successfully: {}, Exit Price: {}, PnL: {}", closeOrder, closePrice, pnl);    }

    @Override
    public BigDecimal getOpenPositionSize(String symbol, AccountInfoDto accountInfo) {
        PositionDto position = getPositionInfo(symbol, accountInfo);
        return position.getPositionValue() != null ? position.getPositionValue() : BigDecimal.ZERO;
    }

    @Override
    public void reversePosition(String symbol, AccountInfoDto accountInfo, Side newSide, BigDecimal quantity, BigDecimal takeProfit, BigDecimal stopLoss) {
        PositionDto position = getPositionInfo(symbol, accountInfo);

        log.info("Quantity to reverse: {}", quantity);
        if (position.getPositionValue() == null || position.getPositionValue().compareTo(BigDecimal.ZERO) == 0) {
            OrderRequestDto newOrder = OrderRequestDto.builder()
                    .symbol(symbol)
                    .side(newSide)
                    .type(TradeOrderType.MARKET)
                    .takeProfit(takeProfit)
                    .stopLoss(stopLoss)
                    .quantity(quantity)
                    .build();

            orderService.placeOrder(newOrder, accountInfo);
            log.info("New order opened successfully: {}", newOrder);
        } else {
            // Если позиция уже открыта, разворачиваем её
            BigDecimal reverseQuantity = quantity.abs().multiply(BigDecimal.TWO);
            log.info("Reversing position for symbol: {}, current position value: {}, reverse quantity: {}", symbol, position.getPositionValue(), reverseQuantity);

            OrderRequestDto reverseOrder = OrderRequestDto.builder()
                    .symbol(symbol)
                    .side(newSide) // Меняем направление
                    .type(TradeOrderType.MARKET)
                    .takeProfit(takeProfit)
                    .stopLoss(stopLoss)
                    .quantity(reverseQuantity)
                    .build();

            orderService.placeOrder(reverseOrder, accountInfo);
            log.info("Position reversed successfully: {}", reverseOrder);
        }
    }

    public BigDecimal calculatePnL(Side side, BigDecimal entryPrice, BigDecimal exitPrice, BigDecimal quantity) {
        if (exitPrice == null) {
            return BigDecimal.ZERO;
        }

        return side == Side.BUY
                ? exitPrice.subtract(entryPrice).multiply(quantity)
                : entryPrice.subtract(exitPrice).multiply(quantity);
    }
}
