//package com.trading.telegram_bot.config.user_session;
//
//import org.springframework.stereotype.Component;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class TelegramUserSessionFactory {
//
//    private final ConcurrentHashMap<Long, TelegramUserSession> sessions = new ConcurrentHashMap<>();
//
//    public TelegramUserSession getSession(Long chatId) {
//        return sessions.computeIfAbsent(chatId, TelegramUserSession::new);
//    }
//}
