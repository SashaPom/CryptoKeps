//package com.trading.bybit_api.service;
//
//import com.bybit.api.client.restApi.BybitApiAccountRestClient;
//import com.trading.bybit_api.config.connection.BybitAccountConnector;
//import com.trading.bybit_api.config.connection.BybitAccountConnectorFactory;
//import com.trading.bybit_api.dto.BalanceDto;
//import com.trading.bybit_api.service.impl.BybitBalanceServiceImpl;
//import com.trading.bybit_api.service.interfaces.IBybitBalanceService;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(classes = {BybitBalanceServiceImpl.class})
//public class BybitBalanceServiceTest {
//
//    @Autowired
//    private IBybitBalanceService balanceService; // тестируемый сервис
//
//    @MockBean
//    private BybitApiAccountRestClient mockApiRestClient; // мок, который будет возвращаться из фиктивного коннектора
//
//    @Test
//    void testFetchBalances() {
//        // Подготовка входных данных
//        String apiKey = "test-api-key";
//        String apiSecret = "test-api-secret";
//
//        // Создаем фиктивный коннектор и задаем поведение его метода getApiRestClient()
//        BybitAccountConnector mockConnector = mock(BybitAccountConnector.class);
//        when(mockConnector.getApiRestClient()).thenReturn(mockApiRestClient);
//
//        // Подготавливаем фиктивный ответ от API.
//        // Предположим, что для монеты "BTC" API возвращает следующую структуру:
//        // {
//        //   "result": {
//        //       "list": [
//        //           {
//        //               "coin": [
//        //                   {
//        //                       "coin": "BTC",
//        //                       "availableToWithdraw": "0.5",
//        //                       "total": "1.0"
//        //                   }
//        //               ]
//        //           }
//        //       ]
//        //   }
//        // }
//        Map<String, Object> dummyResponse = Map.of(
//                "result", Map.of(
//                        "list", List.of(
//                                Map.of("coin", List.of(
//                                        Map.of("coin", "BTC",
//                                                "equity", "0.5",
//                                                "walletBalance", "1.0")
//                                ))
//                        )
//                )
//        );
//
//        // Настраиваем мок apiRestClient: при вызове getWalletBalance(...) возвращаем dummyResponse.
//        when(mockApiRestClient.getWalletBalance(any())).thenReturn(dummyResponse);
//
//        // Мокаем статический метод фабрики BybitAccountConnectorFactory.createConnector(apiKey, apiSecret)
//        try (MockedStatic<BybitAccountConnectorFactory> mockedFactory = mockStatic(BybitAccountConnectorFactory.class)) {
//            mockedFactory.when(() -> BybitAccountConnectorFactory.createConnector(apiKey, apiSecret, anyBoolean()))
//                    .thenReturn(mockConnector);
//
//            // Вызываем тестируемый метод
//            List<BalanceDto> result = balanceService.fetchBalances(apiKey, apiSecret);
//
//            // Проверяем результат: ожидаем, что будет создан один баланс для монеты BTC.
//            assertNotNull(result);
//            assertEquals(1, result.size());
//
//            BalanceDto balanceDto = result.get(0);
//            assertEquals("BTC", balanceDto.getSymbol());
//            assertEquals(new BigDecimal("0.5"), balanceDto.getAvailableBalance());
//            assertEquals(new BigDecimal("1.0"), balanceDto.getTotalBalance());
//        }
//    }
//}
