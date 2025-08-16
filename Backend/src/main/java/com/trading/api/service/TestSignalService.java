package com.trading.api.service;

import com.trading.api.dto.TestSignalRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestSignalService {
    public void send(String botId, TestSignalRequest req) {
        // TODO: тут можно дернуть BybitOrderServiceImpl, если хочешь реально слать на биржу.
        log.info("TEST-SIGNAL bot={}, payload={}", botId, req);
        // В MVP просто логируем и возвращаем 200
    }
}
