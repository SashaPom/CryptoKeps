//package com.trading.telegram_bot.config.user_session;
//
//import com.trading.bybit_api.dto.PositionDto;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Getter
//@Setter
//public class TelegramUserSession {
//
//    private final Long chatId;
//
//    private String apiKey;
//    private String apiSecret;
//
//    // private StrategyType strategy;
//    private PositionDto currentPosition;
//
//    private boolean waitingForConfirmation;
//
//    private final Map<String, Object> stateData = new HashMap<>();
//
//    public TelegramUserSession(Long chatId) {
//        this.chatId = chatId;
//    }
//
//    public void putState(String key, Object value) {
//        stateData.put(key, value);
//    }
//
//    public Object getState(String key) {
//        return stateData.get(key);
//    }
//}