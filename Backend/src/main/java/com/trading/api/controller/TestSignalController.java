package com.trading.api.controller;

import com.trading.api.dto.TestSignalRequest;
import com.trading.api.service.TestSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bots/{botId}/test-signal")
@RequiredArgsConstructor
public class TestSignalController {

    private final TestSignalService service;

    @PostMapping
    public Map<String, Object> send(
            @PathVariable String botId,
            @RequestBody TestSignalRequest req
    ) {
        service.send(botId, req);
        return Map.of("ok", true);
    }
}
