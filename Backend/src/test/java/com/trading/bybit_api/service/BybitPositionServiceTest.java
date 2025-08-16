package com.trading.bybit_api.service;

import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.PositionDto;
import com.trading.bybit_api.service.impl.BybitPositionServiceImpl;
import com.trading.bybit_api.service.interfaces.IBybitPositionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BybitPositionServiceTest {

    @Autowired
    private IBybitPositionService positionService;

    private final String realApiKey = "IChy4VJLtjzwGevPUf";
    private final String realApiSecret = "EaY2x2ikiQSyZaJHB3yxBYQ9FdQjFKydWyqn";

    @Test
    void testGetPositionInfoRealRequest() {
        String symbol = "SOLUSDT";

        AccountInfoDto accountInfo = AccountInfoDto.builder()
                .apiKey(realApiKey)
                .apiSecret(realApiSecret)
                .isConnected(true)
                .build();

        PositionDto position = positionService.getPositionInfo(symbol, accountInfo);

        assertNotNull(position);
        assertEquals(symbol, position.getSymbol());
        assertTrue(position.getPositionValue().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(position.getEntryPrice().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(position.getCurrentPrice().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(position.getLeverage().compareTo(BigDecimal.ZERO) >= 0);

        System.out.println("Position Info:");
        System.out.println("Symbol: " + position.getSymbol());
        System.out.println("Entry Price: " + position.getEntryPrice());
        System.out.println("Current Price: " + position.getCurrentPrice());
        System.out.println("Unrealized PnL: " + position.getUnrealizedPnl());
        System.out.println("Take Profit: " + position.getTakeProfit());
        System.out.println("Stop Loss: " + position.getStopLoss());
        System.out.println("Leverage: " + position.getLeverage());
    }
}
