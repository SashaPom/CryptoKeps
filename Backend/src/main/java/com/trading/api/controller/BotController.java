package com.trading.api.controller;

import com.trading.api.dto.BotCardDto;
import com.trading.api.service.BotQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
@RequiredArgsConstructor
public class BotController {

    private final BotQueryService service;

    @GetMapping
    public List<BotCardDto> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "period", defaultValue = "24h") String period
    ) {
        return service.listBots(q, status, period);
    }
}
