package com.trading.bybit_api.service.interfaces;

import com.trading.bybit_api.dto.BalanceDto;

import java.math.BigDecimal;
import java.util.List;

public interface IBybitBalanceService {
    /**
     * Получает актуальные балансы с биржи по заданным ключам.
     */
    List<BalanceDto> fetchBalances(String apiKey, String apiSecret);

    BigDecimal getTotalBalance(String apiKey, String apiSecret);
}
