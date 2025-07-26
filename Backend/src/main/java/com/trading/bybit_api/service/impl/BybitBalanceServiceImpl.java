package com.trading.bybit_api.service.impl;

import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.restApi.BybitApiAccountRestClient;
import com.trading.bybit_api.config.connection.BybitAccountConnector;
import com.trading.bybit_api.config.connection.BybitAccountConnectorFactory;
import com.trading.bybit_api.config.connection.BybitApiProperties;
import com.trading.bybit_api.dto.BalanceDto;
import com.trading.bybit_api.service.interfaces.IBybitBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




@Slf4j
@Service
@RequiredArgsConstructor
public class BybitBalanceServiceImpl implements IBybitBalanceService {
    private BybitApiProperties properties;

    @Autowired
    public BybitBalanceServiceImpl(BybitApiProperties properties) {
        this.properties = properties;
    }

    private BybitApiAccountRestClient getRestClient(String apiKey, String apiSecret) {
        BybitAccountConnector connector = BybitAccountConnectorFactory.createConnector(apiKey, apiSecret, properties.isUseDemo());
        BybitApiAccountRestClient client = connector.getApiRestClient();
        if (client == null) {
            log.error("Failed to create BybitApiAccountRestClient");
            throw new RuntimeException("Failed to create BybitApiAccountRestClient");
        }
        return client;
    }

    /**
     * Получает текущие балансы аккаунта по монетам с Bybit.
     * https://bybit-exchange.github.io/docs/v5/account/wallet-balance ??????
     */
    @Override
    public List<BalanceDto> fetchBalances(String apiKey, String apiSecret) {
        BybitApiAccountRestClient apiRestClient = getRestClient(apiKey, apiSecret);

        List<BalanceDto> balanceDtoList = new ArrayList<>();

        try {
            AccountDataRequest request = AccountDataRequest.builder()
                    .accountType(AccountType.UNIFIED)
                    .build();

            Map<String, Object> response = (Map<String, Object>) apiRestClient.getWalletBalance(request);

            if (response != null && response.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("list");
                if (list != null) {
                    for (Map<String, Object> balanceInfo : list) {
                        List<Map<String, Object>> coins = (List) balanceInfo.get("coin");
                        for (Map<String, Object> coin : coins) {

                            //Вывод всех значений баланса
                            System.out.println("Balance --- ");
                            for (Map.Entry<String, Object> entry : coin.entrySet()) {
                                System.out.printf("%-20s: %s%n", entry.getKey(), entry.getValue());
                            }
                            System.out.println();

                            balanceDtoList.add(
                                    BalanceDto.builder()
                                            .symbol((String) coin.get("coin"))
                                            .availableBalance(new BigDecimal((String) coin.get("equity")))
                                            .totalBalance(new BigDecimal((String) coin.get("walletBalance")))
                                            .build());
                        }
                    }
                }
            }
            return balanceDtoList;
        } catch (Exception e) {
            log.error("Exception while fetching balance info coins", e);
            throw new RuntimeException("Failed to fetch balance info", e);
        }
    }

    /**
     * Получает общий баланс по всем монетам в эквиваленте USDT
     */
    @Override
    public BigDecimal getTotalBalance(String apiKey, String apiSecret) {
        List<BalanceDto> balanceList = fetchBalances(apiKey, apiSecret);

        BigDecimal totalBalance = balanceList.stream()
                .map(BalanceDto::getTotalBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("=== Общий баланс в эквиваленте USDT: {}", totalBalance);

        return totalBalance;
    }
}
