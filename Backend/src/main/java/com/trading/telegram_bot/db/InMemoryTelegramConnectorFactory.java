//package com.trading.telegram_bot.db;
//
//import com.trading.telegram_bot.config.bot_connection.TelegramBotConnectorFactory;
//import com.trading.telegram_bot.config.user_session.TelegramUserSession;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class InMemoryTelegramConnectorFactory implements TelegramBotConnectorFactory {
//
//    private final Map<Long, TelegramUserSession> sessionStore = new ConcurrentHashMap<>();
//
//    @Override
//    public TelegramUserSession getSession(Long chatId) {
//        return sessionStore.computeIfAbsent(chatId, TelegramUserSession::new);
//    }
//
//    @Override
//    public void saveSession(TelegramUserSession session) {
//        sessionStore.put(session.getChatId(), session);
//    }
//
//    @Override
//    public void clearSession(Long chatId) {
//        sessionStore.remove(chatId);
//    }
//}
