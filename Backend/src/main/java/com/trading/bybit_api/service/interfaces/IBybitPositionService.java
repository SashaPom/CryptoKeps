package com.trading.bybit_api.service.interfaces;

import com.bybit.api.client.domain.trade.Side;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.PositionDto;

import java.math.BigDecimal;

public interface IBybitPositionService {
    void openLongPosition(String symbol, AccountInfoDto accountInfo, BigDecimal entryPrice, BigDecimal quantity, BigDecimal stopLoss, BigDecimal takeProfitPrice);
    PositionDto getPositionInfo(String symbol, AccountInfoDto accountInfo);
    void closePosition(String symbol, AccountInfoDto accountInfo, BigDecimal quantity, BigDecimal closePrice);
    BigDecimal getOpenPositionSize(String symbol, AccountInfoDto accountInfo);
    void reversePosition(String symbol, AccountInfoDto accountInfo, Side newSide, BigDecimal quantity, BigDecimal takeProfit, BigDecimal stopLoss);
}
