package com.trading.bybit_api.service.interfaces;

import com.trading.bybit_api.dto.WebhookSignalDto;

public interface IWebhookService {
    String processSignal(WebhookSignalDto signal);
}
