//package com.trading.bybit_api.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.trading.bybit_api.domain.AccountInfo;
//import com.trading.bybit_api.dto.AccountInfoDto;
//import com.trading.bybit_api.dto.BalanceDto;
//import com.trading.bybit_api.service.interfaces.IBybitAccountInfoService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.math.BigDecimal;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//@AutoConfigureMockMvc
//public class AccountInfoControllerTest extends AbstractIT{
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private IBybitAccountInfoService accountInfoService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private AccountInfoDto accountInfoDto;
//
//    @BeforeEach
//    void setUp() {
//        BalanceDto balanceDto = BalanceDto.builder()
//                .symbol("BTC")
//                .availableBalance(new BigDecimal("0.5"))
//                .totalBalance(new BigDecimal("1.0"))
//                .build();
//        accountInfoDto = AccountInfoDto.builder()
//                .apiKey("test-api-key")
//                .apiSecret("test-api-secret")
//                .isConnected(true)
//                .balances(List.of(balanceDto))
//                .build();
//    }
//
//    @Test
//    void testGetAccountInfo() throws Exception {
//        when(accountInfoService.getAccountInfoById(1L)).thenReturn(accountInfoDto);
//
//        mockMvc.perform(get("/api/v1/account-info/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.apiKey").value("test-api-key"))
//                .andExpect(jsonPath("$.apiSecret").value("test-api-secret"))
//                .andExpect(jsonPath("$.connected").value(true))
//                .andExpect(jsonPath("$.balances[0].symbol").value("BTC"));
//    }
//
//    @Test
//    void testCreateAccountInfo() throws Exception {
//        AccountInfo accountInfo = AccountInfo.builder()
//                .apiKey("test-api-key")
//                .apiSecret("test-api-secret")
//                .isConnected(true)
//                .build();
//
//        when(accountInfoService.createAccountInfo(any(AccountInfo.class))).thenReturn(accountInfoDto);
//
//        mockMvc.perform(post("/api/v1/account-info")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(accountInfo)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.apiKey").value("test-api-key"));
//    }
//
//    @Test
//    void testUpdateAccountInfo() throws Exception {
//        AccountInfo accountInfo = AccountInfo.builder()
//                .apiKey("updated-api-key")
//                .apiSecret("updated-api-secret")
//                .isConnected(false)
//                .build();
//
//        AccountInfoDto updatedDto = AccountInfoDto.builder()
//                .apiKey("updated-api-key")
//                .apiSecret("updated-api-secret")
//                .isConnected(false)
//                .balances(accountInfoDto.getBalances())
//                .build();
//
//        when(accountInfoService.updateAccountInfo(eq(1L), any(AccountInfo.class)))
//                .thenReturn(updatedDto);
//
//        mockMvc.perform(put("/api/v1/account-info/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(accountInfo)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.apiKey").value("updated-api-key"))
//                .andExpect(jsonPath("$.connected").value(false));
//    }
//
//    @Test
//    void testDeleteAccountInfo() throws Exception {
//        doNothing().when(accountInfoService).deleteAccountInfoById(1L);
//
//        mockMvc.perform(delete("/api/v1/account-info/1"))
//                .andExpect(status().isOk());
//    }
//}
