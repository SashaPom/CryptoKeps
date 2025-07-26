package com.trading.bybit_api.service.impl;

import com.trading.bybit_api.domain.AccountInfo;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.dto.BalanceDto;
import com.trading.bybit_api.entity.AccountInfoEntity;
import com.trading.bybit_api.repository.AccountInfoRepository;
import com.trading.bybit_api.service.interfaces.IBybitAccountInfoService;
import com.trading.bybit_api.service.interfaces.IBybitBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BybitAccountInfoServiceImpl implements IBybitAccountInfoService {

    private final AccountInfoRepository accountInfoRepository;
    private final IBybitBalanceService balanceService;

    @Override
    @Transactional(readOnly = true)
    public AccountInfoDto getAccountInfoById(UUID id) {
        // Извлекаем данные подключения из БД по идентификатору
        AccountInfoEntity entity = accountInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AccountInfo not found with id: " + id));

        log.info("Fetched AccountInfoEntity: {}", entity);

        // Запрашиваем актуальные балансы с биржи
        List<BalanceDto> balances = balanceService.fetchBalances(entity.getApiKey(), entity.getApiSecret());

        // Формируем AccountInfoDto вручную
        return AccountInfoDto.builder()
                .keyName(entity.getKeyName())
                .apiKey(entity.getApiKey())
                .apiSecret(entity.getApiSecret())
                .isConnected(entity.isConnected())
                .balances(balances)
                .build();
    }

    @Override
    @Transactional
    public AccountInfoDto createAccountInfo(AccountInfo accountInfo) {
        // Преобразуем доменную модель в сущность
        AccountInfoEntity entity = new AccountInfoEntity();
        entity.setApiKey(accountInfo.getApiKey());
        entity.setApiSecret(accountInfo.getApiSecret());
        entity.setConnected(accountInfo.isConnected());
        // Сохраняем сущность – id генерируется автоматически
        AccountInfoEntity savedEntity = accountInfoRepository.save(entity);

        // Опционально: можно сразу получить актуальные балансы
        List<BalanceDto> balances = balanceService.fetchBalances(savedEntity.getApiKey(), savedEntity.getApiSecret());

        return AccountInfoDto.builder()
                .keyName(accountInfo.getKeyName())
                .apiKey(savedEntity.getApiKey())
                .apiSecret(savedEntity.getApiSecret())
                .isConnected(savedEntity.isConnected())
                .balances(balances)
                .build();
    }

    @Override
    @Transactional
    public AccountInfoDto updateAccountInfo(UUID id, AccountInfo accountInfo) {
        // Находим существующую сущность по id (предполагается, что AccountInfo содержит id)
        AccountInfoEntity existingEntity = accountInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AccountInfo not found with id: " + id));

        // Обновляем поля
        existingEntity.setApiKey(accountInfo.getApiKey());
        existingEntity.setApiSecret(accountInfo.getApiSecret());
        existingEntity.setConnected(accountInfo.isConnected());
        AccountInfoEntity updatedEntity = accountInfoRepository.save(existingEntity);

        // Получаем актуальные балансы с биржи
        List<BalanceDto> balances = balanceService.fetchBalances(updatedEntity.getApiKey(), updatedEntity.getApiSecret());


        return AccountInfoDto.builder()
                .apiKey(updatedEntity.getApiKey())
                .apiSecret(updatedEntity.getApiSecret())
                .keyName(updatedEntity.getKeyName())
                .isConnected(updatedEntity.isConnected())
                .balances(balances)
                .build();
    }

    @Override
    @Transactional
    public void deleteAccountInfoById(UUID id) {
        accountInfoRepository.deleteById(id);
    }
}
