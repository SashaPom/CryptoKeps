package com.trading.bybit_api.service.interfaces;

import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.OrderRequestDto;

public interface ITradeHistoryService {
    void saveTrade(OrderRequestDto orderRequest, AccountInfoDto accountInfo);
}

