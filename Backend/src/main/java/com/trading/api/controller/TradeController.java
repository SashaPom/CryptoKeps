package com.trading.api.controller;

import com.trading.api.dto.TradeRowDto;
import com.trading.api.service.TradeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bots/{botId}/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeQueryService service;

    @GetMapping
    public List<TradeRowDto> recent(
            @PathVariable String botId,
            @RequestParam(value = "period", defaultValue = "24h") String period
    ) {
        return service.recentTrades(botId, period);
    }
}
