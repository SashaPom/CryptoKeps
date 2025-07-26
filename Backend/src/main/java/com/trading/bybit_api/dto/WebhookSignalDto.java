package com.trading.bybit_api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder(toBuilder = true)
public class WebhookSignalDto {
    private String symbol;
    private String side;
    private String orderType;
    private BigDecimal quantity;
    private BigDecimal price;
    private UUID accountId;
    private BigDecimal atr;
    private BigDecimal takeProfit;
    private BigDecimal stopLoss;

    @JsonCreator
    public WebhookSignalDto(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("side") String side,
            @JsonProperty("orderType") String orderType,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("accountId") UUID accountId,
            @JsonProperty("atr") BigDecimal atr,
            @JsonProperty("takeProfit") BigDecimal takeProfit,
            @JsonProperty("stopLoss") BigDecimal stopLoss) {
        this.symbol = symbol;
        this.side = side;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.accountId = accountId;
        this.atr = atr;
        this.takeProfit = takeProfit;
        this.stopLoss = stopLoss;
    }
}
