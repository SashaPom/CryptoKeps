package com.trading.strategy.analyser;

import com.bybit.api.client.domain.market.MarketInterval;
import com.trading.strategy.domain.Kline;
import sun.misc.Signal;

import java.util.List;

public interface Analyser {
    void update(String symbol, MarketInterval interval, Kline klineElement);
    List<Signal> analyze(String symbol, List<MarketInterval> intervals);
}
