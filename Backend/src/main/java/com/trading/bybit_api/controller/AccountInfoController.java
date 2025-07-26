package com.trading.bybit_api.controller;

import com.trading.bybit_api.domain.AccountInfo;
import com.trading.bybit_api.dto.AccountInfoDto;
import com.trading.bybit_api.service.interfaces.IBybitAccountInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account-info")
@RequiredArgsConstructor
public class AccountInfoController {

    private final IBybitAccountInfoService accountInfoService;

    @GetMapping("/{id}")
    public AccountInfoDto getAccountInfo(@PathVariable UUID id) {
        return accountInfoService.getAccountInfoById(id);
    }

    @PostMapping
    public AccountInfoDto createAccountInfo(@RequestBody AccountInfo accountInfo) {
        return accountInfoService.createAccountInfo(accountInfo);
    }

    @PutMapping("/{id}")
    public AccountInfoDto updateAccountInfo(@PathVariable UUID id, @RequestBody AccountInfo accountInfo) {
        return accountInfoService.updateAccountInfo(id, accountInfo);
    }

    @DeleteMapping("/{id}")
    public void deleteAccountInfo(@PathVariable UUID id) {
        accountInfoService.deleteAccountInfoById(id);
    }
}
