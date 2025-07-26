//package com.trading.bybit_api.mapper;
//
//import com.trading.bybit_api.domain.AccountInfo;
//import com.trading.bybit_api.domain.Balance;
//import com.trading.bybit_api.entity.AccountInfoEntity;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Named;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Mapper(componentModel = "spring")
//public interface AccountInfoMapper {
//
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "balances", source = "balances", qualifiedByName = "mapBalanceEntityMap")
//    public abstract AccountInfo toAccountInfo(AccountInfoEntity entity);
//
//    @Mapping(target = "balances", source = "balances", qualifiedByName = "mapBalanceMapToEntity")
//    public abstract AccountInfoEntity toEntity(AccountInfo accountInfo);
//
//    @Named("mapBalanceEntityMap")
//    Map<String, Balance> mapBalanceEntityMap(Map<String, BalanceEntity> balanceEntityMap) {
//        if (balanceEntityMap == null) {
//            return null;
//        }
//        return balanceEntityMap.entrySet()
//                .stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        entry -> balanceMapper.toBalance(entry.getValue())
//                ));
//    }
//
//    @Named("mapBalanceMapToEntity")
//    Map<String, BalanceEntity> mapBalanceMapToEntity(Map<String, Balance> balanceMap) {
//        if (balanceMap == null) {
//            return null;
//        }
//        return balanceMap.entrySet()
//                .stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        entry -> balanceMapper.toEntity(entry.getValue())
//                ));
//    }
//}