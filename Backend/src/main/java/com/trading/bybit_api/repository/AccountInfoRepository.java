package com.trading.bybit_api.repository;

import com.trading.bybit_api.entity.AccountInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfoEntity, UUID> {
    Optional<AccountInfoEntity> findByKeyName(String keyName);
    Optional<AccountInfoEntity> findByApiKey(String apiKey);
}
