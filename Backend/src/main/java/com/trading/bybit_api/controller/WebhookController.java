package com.trading.bybit_api.controller;

import com.trading.bybit_api.dto.WebhookSignalDto;
import com.trading.bybit_api.service.interfaces.IBybitAccountInfoService;
import com.trading.bybit_api.service.interfaces.IBybitBalanceService;
import com.trading.bybit_api.service.interfaces.IBybitPositionService;
import com.trading.bybit_api.service.interfaces.IWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final IWebhookService webhookService;

    @PostMapping("/signal")
    public ResponseEntity<String> processTradingSignal(@RequestBody WebhookSignalDto signal) {
        String responseMessage = webhookService.processSignal(signal);
        return ResponseEntity.ok(responseMessage);
    }
}

