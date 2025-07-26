package com.trading.bybit_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.restApi.BybitApiPositionRestClient;
import com.trading.bybit_api.config.connection.BybitAccountConnector;
import com.trading.bybit_api.config.connection.BybitAccountConnectorFactory;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.PositionDto;
import com.trading.bybit_api.service.impl.BybitPositionServiceImpl;
import com.trading.bybit_api.service.interfaces.IBybitPositionService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {BybitPositionServiceImpl.class})
public class BybitPositionServiceTest {

    @Autowired
    private IBybitPositionService positionService;

    @MockBean
    private BybitApiPositionRestClient mockPositionRestClient;

    private final String testApiKey = "test-api-key";
    private final String testApiSecret = "test-api-secret";

    @Test
    void testGetPositionInfo() throws Exception {
        String symbol = "BTCUSDT";

        // Подготовка поддельного ответа от API
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("symbol", symbol);
        resultMap.put("size", "0.1");
        resultMap.put("positionValue", "3000");
        resultMap.put("avgPrice", "30000");
        resultMap.put("markPrice", "31000");
        resultMap.put("unrealisedPnl", "100");
        resultMap.put("takeProfit", "32000");
        resultMap.put("stopLoss", "29000");
        resultMap.put("leverage", "10");

        Map<String, Object> dummyResponse = new HashMap<>();
        dummyResponse.put("result", resultMap);

        // Мокаем поведение клиента: при вызове getPositionInfo(...) возвращаем dummyResponse
        when(mockPositionRestClient.getPositionInfo(any(PositionDataRequest.class)))
                .thenReturn(dummyResponse);

        // Мокаем статический метод фабрики, чтобы вернуть фиктивный коннектор с нашим замоканным клиентом
        BybitAccountConnector mockConnector = org.mockito.Mockito.mock(BybitAccountConnector.class);
        when(mockConnector.getPositionRestClient()).thenReturn(mockPositionRestClient);

        try (MockedStatic<BybitAccountConnectorFactory> mockedFactory =
                     mockStatic(BybitAccountConnectorFactory.class)) {
            mockedFactory.when(() -> BybitAccountConnectorFactory.createConnector(testApiKey, testApiSecret, true))
                    .thenReturn(mockConnector);

            AccountInfoDto accountInfo = AccountInfoDto.builder()
                    .apiKey(testApiKey)
                    .apiSecret(testApiSecret)
                    .isConnected(true)
                    .balances(null)
                    .build();

            PositionDto position = positionService.getPositionInfo(symbol, accountInfo);

            assertNotNull(position);
            assertEquals(symbol, position.getSymbol());
            assertEquals(new BigDecimal("3000"), position.getPositionValue());
            assertEquals(new BigDecimal("30000"), position.getEntryPrice());
            assertEquals(new BigDecimal("31000"), position.getCurrentPrice());
            assertEquals(new BigDecimal("100"), position.getUnrealizedPnl());
            assertEquals(new BigDecimal("32000"), position.getTakeProfit());
            assertEquals(new BigDecimal("29000"), position.getStopLoss());
            assertEquals(new BigDecimal("10"), position.getLeverage());
        }
    }
}
