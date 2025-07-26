package com.trading.bybit_api.repository;

import com.trading.bybit_api.entity.TradeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistoryEntity, UUID> {
    @Query("SELECT t FROM TradeHistoryEntity t WHERE t.account.id = :accountId AND t.symbol = :symbol AND t.closePrice IS NULL ORDER BY t.timestamp DESC LIMIT 1")
    Optional<TradeHistoryEntity> findLastTrade(@Param("accountId") UUID accountId, @Param("symbol") String symbol);
    @Query("SELECT t FROM TradeHistoryEntity t WHERE t.account.id = :accountId AND t.closePrice IS NOT NULL")
    List<TradeHistoryEntity> findClosedTrades(@Param("accountId") UUID accountId);
}