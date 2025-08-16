package com.trading.bybit_api.service.impl;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.TimeInForce;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.trading.bybit_api.config.connection.BybitAccountConnector;
import com.trading.bybit_api.config.connection.BybitAccountConnectorFactory;
import com.trading.bybit_api.config.connection.BybitApiProperties;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.OrderRequestDto;
import com.trading.bybit_api.dto.OrderResponseDto;
import com.trading.bybit_api.exception.OrderPlacementException;
import com.trading.bybit_api.service.interfaces.IBybitOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BybitOrderServiceImpl implements IBybitOrderService {
    private BybitApiProperties properties;

    private static final List<String> ERROR_WORDS = List.of("error", "invalid", "not found", "insufficient", "limit exceeded");

    @Autowired
    public BybitOrderServiceImpl(BybitApiProperties properties) {
        this.properties = properties;
    }

    private BybitApiTradeRestClient getRestClient(String apiKey, String apiSecret) {
        BybitAccountConnector connector = BybitAccountConnectorFactory.createConnector(apiKey, apiSecret, properties.isUseDemo());
        BybitApiTradeRestClient client = connector.getTradeRestClient();
        if (client == null) {
            log.error("Failed to create BybitApiTradeRestClient");
            throw new RuntimeException("Failed to create BybitApiTradeRestClient");
        }
        return client;
    }

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto request, AccountInfoDto accountInfo) {
        BybitApiTradeRestClient tradeRestClient = getRestClient(accountInfo.getApiKey(), accountInfo.getApiSecret());
        OrderResponseDto responseDto = OrderResponseDto.builder().build();

        try {
            TradeOrderRequest orderRequest = createTradeOrderRequest(request);
            // Оборачиваем синхронный вызов в CompletableFuture для асинхронной обработки
            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> tradeRestClient.createOrder(orderRequest));

            Object response = future.get(); // Можно использовать thenApply/thenAccept для дальнейшей асинхронной обработки

            if (response instanceof Map) {
                log.info("Response on an attempt for place an order: {}", response);
                Map<String, Object> responseMap = (Map<String, Object>) response;

                if (responseMap.containsKey("retMsg")) {
                    String retMsg = String.valueOf(responseMap.get("retMsg"));

                    // Проверяем, есть ли ошибка в `retMsg`
                    if (!retMsg.contains("OK") && ERROR_WORDS.stream().anyMatch(retMsg::contains)) {
                        log.error("Error placing order: {}", retMsg);
                        throw new OrderPlacementException("Error placing order: " + retMsg);
                    } else {
                        log.info("Order placed successfully: {}", response);
                    }

                    Map<String, Object> result = (Map<String, Object>) responseMap.get("result");
                    if (result != null && result.containsKey("orderId")) {
                        String orderId = (String) result.get("orderId");
                        if (orderId != null && !orderId.isEmpty()) {
                            responseDto = responseDto.toBuilder()
                                    .orderId(UUID.fromString(orderId))
                                    //.status("placed")
                                    .build();
                        } else {
                            log.error("Order ID not found in response");
                        }
                    } else {
                        log.error("Result not found in response");
                    }
                } else {
                    log.error("Missing `retMsg` in response");
                }
            } else {
                log.error("Unexpected response format");
            }
        } catch (Exception e) {
            log.error("Exception while placing limit order for symbol: {}", request.getSymbol(), e);
            throw new RuntimeException("Failed to place order", e);
        }

        return responseDto;
    }


    @Override
    public OrderResponseDto updateOrder(OrderRequestDto request, AccountInfoDto accountInfo) {
        BybitApiTradeRestClient tradeRestClient = getRestClient(accountInfo.getApiKey(), accountInfo.getApiSecret());

        OrderResponseDto responseDto = OrderResponseDto.builder().build();

        try {
            TradeOrderRequest updateRequest = TradeOrderRequest.builder()
                    .category(CategoryType.LINEAR)
                    .orderId(String.valueOf(request.getOrderId()))
                    .symbol(request.getSymbol())
                    .price(String.valueOf(request.getPrice()))
                    .takeProfit(String.valueOf(request.getTakeProfit()))
                    .stopLoss(String.valueOf(request.getStopLoss()))
                    .build();

            // Оборачиваем синхронный вызов в CompletableFuture для асинхронной обработки
            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> tradeRestClient.amendOrder(updateRequest));

            Object response = future.get(); // Можно использовать thenApply/thenAccept для асинхронной обработки

            if (response instanceof Map) {
                log.info("Order updated successfully: {}", response);
                Map<String, Object> responseMap = (Map<String, Object>) response;
                if (responseMap.containsKey("result")) {
                    Map<String, Object> result = (Map<String, Object>) responseMap.get("result");
                    String orderId = (String) result.get("orderId");
                    if (orderId != null && !orderId.isEmpty()) {
                        responseDto = responseDto.toBuilder()
                                .orderId(UUID.fromString(orderId))
                                //.status("updated")
                                .build();
                    } else {
                        log.error("OrderUPD ID not found in response");
                    }
                } else {
                    log.error("Result not found in response");
                }
            } else {
                log.error("Unexpected response format");
            }
        } catch (Exception e) {
            log.error("Exception while updating order for symbol: {}", request.getSymbol(), e);
            throw new RuntimeException("Failed to update order", e);
        }

        return responseDto;
    }


    @Override
    public void cancelOrder(OrderRequestDto requestDto, AccountInfoDto accountInfo) {
        BybitApiTradeRestClient tradeRestClient = getRestClient(accountInfo.getApiKey(), accountInfo.getApiSecret());

        try {
            TradeOrderRequest cancelOrderRequest = TradeOrderRequest.builder()
                    .category(CategoryType.LINEAR)
                    .symbol(requestDto.getSymbol())
                    .orderId(String.valueOf(requestDto.getOrderId()))
                    .build();

            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> tradeRestClient.cancelOrder(cancelOrderRequest));

            Object response = future.get();
            log.info("Order canceled successfully: {}", response);
        } catch (Exception e) {
            log.error("Exception while canceling order with orderId: {}", requestDto.getOrderId(), e);
            throw new RuntimeException("Failed to cancel order", e);
        }
    }


    public static TradeOrderRequest createTradeOrderRequest(OrderRequestDto trade) {
        return TradeOrderRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(trade.getSymbol())
                .side(Side.valueOf(trade.getSide().getTransactionSide().toUpperCase()))
                .orderType(trade.getType())
                .qty(String.valueOf(trade.getQuantity()))
                .price(String.valueOf(trade.getPrice()))
                .timeInForce(TimeInForce.GTC)
                .takeProfit(trade.getTakeProfit() != null ? String.valueOf(trade.getTakeProfit()) : null) // Проверка null
                .stopLoss(trade.getStopLoss() != null ? String.valueOf(trade.getStopLoss()) : null) // Проверка null
                .isLeverage(1)
                .build();
    }
}
