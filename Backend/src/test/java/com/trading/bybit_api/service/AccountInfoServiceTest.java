//package com.trading.bybit_api.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import com.trading.bybit_api.domain.AccountInfo;
//import com.trading.bybit_api.dto.AccountInfoDto;
//import com.trading.bybit_api.dto.BalanceDto;
//import com.trading.bybit_api.entity.AccountInfoEntity;
//import com.trading.bybit_api.repository.AccountInfoRepository;
//import com.trading.bybit_api.service.impl.BybitAccountInfoServiceImpl;
//import com.trading.bybit_api.service.interfaces.IBybitAccountInfoService;
//import com.trading.bybit_api.service.interfaces.IBybitBalanceService;
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//@SpringBootTest(classes = {BybitAccountInfoServiceImpl.class})
//public class AccountInfoServiceTest {
//
//    @Autowired
//    private IBybitAccountInfoService accountInfoService;
//
//    @MockBean
//    private AccountInfoRepository accountInfoRepository;
//
//    @MockBean
//    private IBybitBalanceService balanceService;
//
//    private AccountInfoEntity accountEntity;
//    private AccountInfo accountInfo;
//    private List<BalanceDto> balances;
//
//    @BeforeEach
//    void setUp() {
//        accountEntity = new AccountInfoEntity();
//        accountEntity.setId(1L);
//        accountEntity.setApiKey("test-api-key");
//        accountEntity.setApiSecret("test-api-secret");
//        accountEntity.setConnected(true);
//
//        accountInfo = AccountInfo.builder()
//                .apiKey("test-api-key")
//                .apiSecret("test-api-secret")
//                .isConnected(true)
//                .build();
//
//        BalanceDto balanceDto = BalanceDto.builder()
//                .symbol("BTC")
//                .availableBalance(new BigDecimal("0.5"))
//                .totalBalance(new BigDecimal("1.0"))
//                .build();
//        balances = List.of(balanceDto);
//    }
//
//    @Test
//    void testGetAccountInfoById() {
//        when(accountInfoRepository.findById(1L)).thenReturn(Optional.of(accountEntity));
//        when(balanceService.fetchBalances(accountEntity.getApiKey(), accountEntity.getApiSecret()))
//                .thenReturn(balances);
//
//        AccountInfoDto result = accountInfoService.getAccountInfoById(1L);
//
//        assertNotNull(result);
//        assertEquals("test-api-key", result.getApiKey());
//        assertEquals("test-api-secret", result.getApiSecret());
//        assertTrue(result.isConnected());
//        assertEquals(balances, result.getBalances());
//    }
//
//    @Test
//    void testCreateAccountInfo() {
//        // Симулируем сохранение сущности, где id генерируется
//        AccountInfoEntity savedEntity = new AccountInfoEntity();
//        savedEntity.setId(2L);
//        savedEntity.setApiKey(accountInfo.getApiKey());
//        savedEntity.setApiSecret(accountInfo.getApiSecret());
//        savedEntity.setConnected(accountInfo.isConnected());
//
//        when(accountInfoRepository.save(any(AccountInfoEntity.class))).thenReturn(savedEntity);
//        when(balanceService.fetchBalances(savedEntity.getApiKey(), savedEntity.getApiSecret()))
//                .thenReturn(balances);
//
//        AccountInfoDto result = accountInfoService.createAccountInfo(accountInfo);
//
//        assertNotNull(result);
//        assertEquals("test-api-key", result.getApiKey());
//        assertEquals("test-api-secret", result.getApiSecret());
//        assertTrue(result.isConnected());
//        assertEquals(balances, result.getBalances());
//    }
//
//    @Test
//    void testUpdateAccountInfo() {
//        // Сначала имитируем, что сущность существует в базе
//        when(accountInfoRepository.findById(1L)).thenReturn(Optional.of(accountEntity));
//
//        // Обновлённые данные
//        AccountInfo updatedInfo = AccountInfo.builder()
//                .apiKey("updated-api-key")
//                .apiSecret("updated-api-secret")
//                .isConnected(false)
//                .build();
//
//        AccountInfoEntity updatedEntity = new AccountInfoEntity();
//        updatedEntity.setId(1L);
//        updatedEntity.setApiKey(updatedInfo.getApiKey());
//        updatedEntity.setApiSecret(updatedInfo.getApiSecret());
//        updatedEntity.setConnected(updatedInfo.isConnected());
//
//        when(accountInfoRepository.save(accountEntity)).thenReturn(updatedEntity);
//        when(balanceService.fetchBalances(updatedEntity.getApiKey(), updatedEntity.getApiSecret()))
//                .thenReturn(balances);
//
//        AccountInfoDto result = accountInfoService.updateAccountInfo(1L, updatedInfo);
//
//        assertNotNull(result);
//        assertEquals("updated-api-key", result.getApiKey());
//        assertEquals("updated-api-secret", result.getApiSecret());
//        assertFalse(result.isConnected());
//        assertEquals(balances, result.getBalances());
//    }
//
//    @Test
//    void testDeleteAccountInfoById() {
//        // Просто вызываем метод и проверяем, что репозиторий получил вызов deleteById
//        accountInfoService.deleteAccountInfoById(1L);
//        verify(accountInfoRepository, times(1)).deleteById(1L);
//    }
//}
