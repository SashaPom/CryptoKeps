package com.trading.bybit_api.service;

import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiTradeRestClient;
import com.trading.bybit_api.config.connection.BybitAccountConnector;
import com.trading.bybit_api.config.connection.BybitAccountConnectorFactory;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.OrderRequestDto;
import com.trading.bybit_api.dto.OrderResponseDto;
import com.trading.bybit_api.service.impl.BybitOrderServiceImpl;
import com.trading.bybit_api.service.interfaces.IBybitOrderService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BybitOrderServiceImpl.class})
public class BybitOrderServiceTest {

    @Autowired
    private IBybitOrderService orderService;

    @Mock
    private BybitApiTradeRestClient mockTradeRestClient;

    private final String testApiKey = "test-api-key";
    private final String testApiSecret = "test-api-secret";

    @Test
    void testPlaceLimitOrder() {
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .symbol("BTCUSDT")
                .side(Side.BUY)
                .type(TradeOrderType.LIMIT)
                .quantity(new BigDecimal("0.1"))
                .price(new BigDecimal("30000"))
                .takeProfit(new BigDecimal("31000"))
                .stopLoss(new BigDecimal("29000"))
                .build();
        AccountInfoDto accountInfo = AccountInfoDto.builder()
                .apiKey(testApiKey)
                .apiSecret(testApiSecret)
                .isConnected(true)
                .balances(null)
                .build();

        Map<String, Object> result = new HashMap<>();
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        result.put("orderId", uuidString);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", result);

        when(mockTradeRestClient.createOrder(any(TradeOrderRequest.class))).thenReturn(responseMap);

        BybitAccountConnector mockConnector = org.mockito.Mockito.mock(BybitAccountConnector.class);
        when(mockConnector.getTradeRestClient()).thenReturn(mockTradeRestClient);

        try (MockedStatic<BybitAccountConnectorFactory> mockedFactory =
                     org.mockito.Mockito.mockStatic(BybitAccountConnectorFactory.class)) {
            mockedFactory.when(() -> BybitAccountConnectorFactory.createConnector(testApiKey, testApiSecret, true))
                    .thenReturn(mockConnector);

            OrderResponseDto response = orderService.placeOrder(requestDto, accountInfo);

            assertNotNull(response);
            assertEquals(UUID.fromString(uuidString), response.getOrderId());
        }
    }

    @Test
    void testUpdateOrder() {
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .orderId(UUID.randomUUID())
                .symbol("BTCUSDT")
                .side(Side.SELL)
                .type(TradeOrderType.LIMIT)
                .quantity(new BigDecimal("0.1"))
                .price(new BigDecimal("32000"))
                .takeProfit(new BigDecimal("31000"))
                .stopLoss(new BigDecimal("33000"))
                .build();
        AccountInfoDto accountInfo = AccountInfoDto.builder()
                .apiKey(testApiKey)
                .apiSecret(testApiSecret)
                .isConnected(true)
                .balances(null)
                .build();

        Map<String, Object> result = new HashMap<>();
        String uuidString = "223e4567-e89b-12d3-a456-426614174001";
        result.put("orderId", uuidString);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", result);

        when(mockTradeRestClient.amendOrder(any(TradeOrderRequest.class))).thenReturn(responseMap);

        BybitAccountConnector mockConnector = org.mockito.Mockito.mock(BybitAccountConnector.class);
        when(mockConnector.getTradeRestClient()).thenReturn(mockTradeRestClient);

        try (MockedStatic<BybitAccountConnectorFactory> mockedFactory =
                     org.mockito.Mockito.mockStatic(BybitAccountConnectorFactory.class)) {
            mockedFactory.when(() -> BybitAccountConnectorFactory.createConnector(testApiKey, testApiSecret, true))
                    .thenReturn(mockConnector);

            OrderResponseDto response = orderService.updateOrder(requestDto, accountInfo);

            assertNotNull(response);
            assertEquals(UUID.fromString(uuidString), response.getOrderId());
        }
    }

    @Test
    void testCancelOrder() {
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .orderId(UUID.randomUUID())
                .symbol("BTCUSDT")
                .build();
        AccountInfoDto accountInfo = AccountInfoDto.builder()
                .apiKey(testApiKey)
                .apiSecret(testApiSecret)
                .isConnected(true)
                .balances(null)
                .build();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", "Order canceled");

        when(mockTradeRestClient.cancelOrder(any(TradeOrderRequest.class))).thenReturn(responseMap);

        orderService.cancelOrder(requestDto, accountInfo);
    }
}
