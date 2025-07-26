package com.trading.bybit_api.service.interfaces;

import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.OrderRequestDto;
import com.trading.bybit_api.dto.OrderResponseDto;

public interface IBybitOrderService {
    OrderResponseDto placeOrder(OrderRequestDto request, AccountInfoDto accountInfo);
    OrderResponseDto updateOrder(OrderRequestDto request, AccountInfoDto accountInfo);
    void cancelOrder(OrderRequestDto requestDto, AccountInfoDto accountInfo);
}
