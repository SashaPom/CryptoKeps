package com.trading.bybit_api.service.impl;

import com.trading.bybit_api.dto.WebhookSignalDto;
import com.trading.bybit_api.service.interfaces.IBybitAccountInfoService;
import com.trading.bybit_api.service.interfaces.IBybitBalanceService;
import com.trading.bybit_api.service.interfaces.IBybitPositionService;
import com.trading.bybit_api.service.interfaces.IWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements IWebhookService {

    private static final BigDecimal RISK_PERCENTAGE = BigDecimal.valueOf(0.005); // 0.5% от депозита
    private static final BigDecimal TAKE_STOP_RELATION = BigDecimal.valueOf(100); // Забираем 100 к 1
    private static final BigDecimal STOP_MULTIPLAYER = BigDecimal.valueOf(1); // Умножитель для стоп-лосса (X2 от АТР)
    public static final BigDecimal LEVERAGE = BigDecimal.valueOf(2);

    private final IBybitPositionService positionService;
    private final IBybitBalanceService balanceService;
    private final IBybitAccountInfoService accountInfoService;

    @Override
    public String processSignal(WebhookSignalDto signal) {
        log.info("Processing signal: {}", signal);
        try {
            String adjustedSymbol = signal.getSymbol().replace(".P", "");
            var accountInfo = accountInfoService.getAccountInfoById(signal.getAccountId());
            log.info("Account info retrieved: {}", accountInfo);

            BigDecimal currentPosition = positionService.getOpenPositionSize(adjustedSymbol, accountInfo);
            BigDecimal atrInUsd = signal.getAtr();

            if(atrInUsd.equals(BigDecimal.ZERO)) {
                log.error("ATR value is zero, cannot proceed with trade, converting it to 1");
                atrInUsd = BigDecimal.ONE;
            }

            if(atrInUsd.compareTo(BigDecimal.valueOf(0.1)) < 0) {
                log.error("ATR value is less than 0.1, cannot proceed with trade, converting it to 0.1");
                atrInUsd = BigDecimal.valueOf(0.1);
            }

            BigDecimal entryPrice = signal.getPrice();

            log.info("Entry price: {}, ATR: {}", entryPrice, atrInUsd);
            if (entryPrice == null) {
                log.error("Entry price is null, skipping trade history save.");
                return "Invalid signal: Entry price is missing.";
            }

            // Пример расчета позиции:

            // Баланc аккаунта: 100 USD
            // Цена монеты (entryPrice): 180 USD
            // stopSize = ATR (волатильность): 0.1 USD ~ (0.5 * 100 / 180) = 0.27% от цены монетки
            // RISK_PERCENTAGE: 0.005 → то есть 0.5% риска

            // stopLossAmount = 100 × 0.005 = 0.5 USD

            // quantity = stopLossAmount / ATR =
            //            = 0.5 / 0.1 =
            //            = 5 монет

            // stopLossPrice = entryPrice − stopSize
            //              = 180.00 − 0.5
            //              = 179.5 USD

            // takeProfitPrice = entryPrice + ATR × TAKE_STOP_RELATION
            //                = 180.00 + 0.5 × 5
            //                = 182.5 USD

            BigDecimal totalBalance = balanceService.getTotalBalance(accountInfo.getApiKey(), accountInfo.getApiSecret());

            BigDecimal stopLossAmount = totalBalance.multiply(RISK_PERCENTAGE);

            // BigDecimal multipliedStop = atrInUsd.multiply(STOP_MULTIPLAYER);

            BigDecimal quantity = stopLossAmount.divide(atrInUsd, 2, RoundingMode.HALF_UP);

            if (signal.getSide().equalsIgnoreCase("buy")) {
                if (currentPosition.compareTo(BigDecimal.ZERO) > 0) {
                    log.warn("Skipping buy, position already open.");
                    return "Skipped duplicate buy signal.";
                }

                // Цена стоп-лосса относительно цены монетки
                BigDecimal stopLossPrice = entryPrice.subtract(atrInUsd);

                // Тейк нужен, чтобы не  выбрасывалось исключение. Поэтому ставим 1к5
                BigDecimal takeProfitPrice = entryPrice.add(atrInUsd.multiply(TAKE_STOP_RELATION));

                log.info("Opening long: Stop Loss = {}", stopLossPrice);

                // Обязятельно в формате 1.0, иначе Bybit не принимает ордер
                positionService.openLongPosition(adjustedSymbol, accountInfo, entryPrice, quantity.setScale(1, RoundingMode.HALF_UP), stopLossPrice, takeProfitPrice);                return "Long position opened successfully.";
            }

            if (signal.getSide().equalsIgnoreCase("sell")) {
                if (currentPosition.compareTo(BigDecimal.ZERO) == 0) {
                    log.warn("Skipping sell, no open position.");
                    return "Skipped duplicate sell signal.";
                }

                if (totalBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    log.error("Invalid quantity for sell signal: {}", totalBalance);
                    return "Invalid signal: Quantity is missing or zero.";
                }

                log.info("Closing position: Selling {} of {}", totalBalance, adjustedSymbol);
                positionService.closePosition(adjustedSymbol, accountInfo, quantity.setScale(1, RoundingMode.HALF_UP), entryPrice);
                return "Closed position successfully.";
            }

            return "Invalid signal side: " + signal.getSide();
        } catch (Exception e) {
            log.error("Error processing trading signal", e);
            return "Failed to process signal.";
        }
    }
}
