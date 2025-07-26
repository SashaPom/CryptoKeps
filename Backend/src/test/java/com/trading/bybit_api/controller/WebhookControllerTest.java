//package com.trading.bybit_api.controller;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.bybit.api.client.domain.TradeOrderType;
//import com.bybit.api.client.domain.trade.Side;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.trading.bybit_api.dto.WebhookSignalDto;
//import com.trading.bybit_api.service.interfaces.IBybitPositionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//
//@AutoConfigureMockMvc
//public class WebhookControllerTest extends AbstractIT {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private IBybitPositionService positionService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private WebhookSignalDto webhookSignalDto;
//
//    @BeforeEach
//    void setUp() {
//        webhookSignalDto = WebhookSignalDto.builder()
//                .symbol("SOLUSDT.P")
//                .side("sell") // Имитируем входящее значение от TradingView
//                .orderType(TradeOrderType.MARKET)
//                .quantity(new BigDecimal("2"))
//                .price(new BigDecimal("156.47"))
//                .accountId(1L)
//                .build();
//    }
//
//    @Test
//    void testWebhookReceivesTradingSignal() throws Exception {
//        // JSON-сигнал, который приходит от TradingView
//        String tradingSignal = """
//        {
//            "symbol": "SOLUSDT.P",
//            "side": "buy", // Приходит в lowercase
//            "orderType": "MARKET",
//            "quantity": "2",
//            "price": "156.47",
//            "accountId": 1
//        }
//        """;
//
//        mockMvc.perform(post("/api/v1/webhook/signal")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(tradingSignal))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("Position handled successfully")));
//
//        verify(positionService, times(1))
//                .reversePosition(eq("SOLUSDT.P"), any(), Side.valueOf(eq("BUY")), eq(new BigDecimal("2")), takeProfit, stopLoss); // Проверяем корректное преобразование "buy" → "BUY"
//    }
//}
