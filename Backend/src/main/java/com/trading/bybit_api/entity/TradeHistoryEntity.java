package com.trading.bybit_api.entity;

import com.bybit.api.client.domain.trade.Side;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trade_history")
public class TradeHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountInfoEntity account;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private Side side;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "entry_price")
    private BigDecimal entryPrice;

    @Column(name = "close_price")
    private BigDecimal closePrice;

    @Column(name = "stop_loss")
    private BigDecimal stopLoss;

    @Column(name = "take_profit")
    private BigDecimal takeProfit;

    @Column(name = "pnl")
    private BigDecimal pnl;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
