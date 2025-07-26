package com.trading;

import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.trade.Side;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.OrderRequestDto;
import com.trading.bybit_api.dto.OrderResponseDto;
import com.trading.bybit_api.dto.PositionDto;
import com.trading.bybit_api.service.interfaces.IBybitAccountInfoService;
import com.trading.bybit_api.service.interfaces.IBybitOrderService;
import com.trading.bybit_api.service.interfaces.IBybitPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootApplication
public class CryptoKepsApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(CryptoKepsApiApplication.class, args);
  }
}

//@SpringBootApplication
//public class CryptoKepsApiApplication implements CommandLineRunner {
//
//  public static void main(String[] args) {
//    SpringApplication.run(CryptoKepsApiApplication.class, args);
//  }
//
//  private final IBybitAccountInfoService accountInfoService;
//  private final IBybitOrderService orderService;
//  private final IBybitPositionService positionService;
//
//
//  @Autowired
//  public CryptoKepsApiApplication(IBybitAccountInfoService accountInfoService, IBybitOrderService orderService, IBybitPositionService positionService) {
//    this.accountInfoService = accountInfoService;
//    this.orderService = orderService;
//    this.positionService = positionService;
//  }
//
//  @Override
//  public void run(String... args) throws Exception {
//    RestTemplate restTemplate = new RestTemplate();
//
//    // 1. Проверка получения инфо об аккаунте на Bybit
//    String accInfoUrl = "http://localhost:8080/api/v1/account-info/64312ca0-37c4-43eb-babe-204be1be94a9";
//    ResponseEntity<String> response = restTemplate.getForEntity(accInfoUrl, String.class);
//    System.out.println("HTTP Status: " + response.getStatusCode());
//    System.out.println("Response Body: " + response.getBody());
//
//    // 2. Проверка метода размещения, обновления, отмены ордера
//    OrderRequestDto orderRequest = OrderRequestDto.builder()
//            .symbol("BTCUSDT")
//            .side(Side.BUY)
//            .type(TradeOrderType.LIMIT)
//            .quantity(new BigDecimal("0.1"))
//            .price(new BigDecimal("30000"))
//            .takeProfit(new BigDecimal("31000"))
//            .stopLoss(new BigDecimal("29000"))
//            .build();
//
//    AccountInfoDto accountInfo = accountInfoService.getAccountInfoById(UUID.fromString("64312ca0-37c4-43eb-babe-204be1be94a9"));
//
//    // Размещение ордера
//    OrderResponseDto placeResponse = orderService.placeOrder(orderRequest, accountInfo);
//    System.out.println("Place order response: " + placeResponse);
//
//    // Обновление ордера: предположим, что orderId получен из placeResponse
//    OrderRequestDto updateRequest = orderRequest.toBuilder()
//            .orderId(placeResponse.getOrderId())
//            .price(new BigDecimal("30500"))
//            .build();
//    OrderResponseDto updateResponse = orderService.updateOrder(updateRequest, accountInfo);
//    // Выводит null так как не мы не можем напрямую вытащить orderId из placeResponse
//    // Тем не менее, все работает коректно
//    System.out.println("Update order response: " + updateResponse);
//
//    // Отмена ордера (используем обновленный request)
////    orderService.cancelOrder(updateRequest, accountInfo);
////    System.out.println("Order canceled successfully");
//
//    // 3. Проверка получения информации о позиции
//    PositionDto position = positionService.getPositionInfo("BTCUSDT", accountInfo);
//    System.out.println("Position info: " + position);
//  }
//}
