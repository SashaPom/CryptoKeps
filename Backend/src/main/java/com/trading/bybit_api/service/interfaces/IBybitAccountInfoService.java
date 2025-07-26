package com.trading.bybit_api.service.interfaces;

import com.trading.bybit_api.domain.AccountInfo;
import com.trading.bybit_api.dto.AccountInfoDto;

import java.util.UUID;

public interface IBybitAccountInfoService {
    AccountInfoDto getAccountInfoById(UUID id);
    AccountInfoDto createAccountInfo(AccountInfo accountInfo);
    AccountInfoDto updateAccountInfo(UUID id, AccountInfo accountInfo);
    void deleteAccountInfoById(UUID id);
}
