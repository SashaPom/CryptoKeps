//package com.trading.telegram_bot.service;
//
//import com.bybit.api.client.domain.TradeOrderType;
//import com.trading.bybit_api.dto.WebhookSignalDto;
//import com.trading.bybit_api.service.impl.BybitPositionServiceImpl;
//import com.trading.telegram_bot.config.user_session.TelegramUserSession;
//import com.trading.telegram_bot.config.user_session.TelegramUserSessionFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.ta4j.core.Order;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.meta.bots.AbsSender;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class TelegramBotService {
//
//    private final TelegramUserSessionFactory userSessionFactory;
//
//    @Autowired
//    private final BybitPositionServiceImpl bybitPositionService;
//
//    private final TradeController tradeController;
//
//    private AbsSender sender;
//
//    public void configureSender(AbsSender sender) {
//        this.sender = sender;
//    }
//
//    public void handleUpdate(Update update) {
//        if (!update.hasMessage() || !update.getMessage().hasText()) return;
//
//        Message message = update.getMessage();
//        Long chatId = message.getChatId();
//        String text = message.getText().trim();
//
//        TelegramUserSession session = userSessionFactory.getSession(chatId);
//
//        if (text.equalsIgnoreCase("/start")) {
//            sendMessage(chatId, "Добро пожаловать! Доступные команды: /order, /close");
//            return;
//        }
//
//        if (text.startsWith("/order")) {
//            handleOrder(chatId, text, session);
//            return;
//        }
//
//        if (text.equalsIgnoreCase("/close")) {
//            String result = BybitPositionServiceImplde(chatId);
//            sendMessage(chatId, result);
//            return;
//        }
//
//        sendMessage(chatId, "Неизвестная команда. Пример: /order BTCUSDT buy 38500 75");
//    }
//
//    private void handleOrder(Long chatId, String text, TelegramUserSession session) {
//        String[] parts = text.split("\\s+");
//        if (parts.length < 5) {
//            sendMessage(chatId, "Формат команды неверен. Используйте: /order SYMBOL SIDE PRICE ATR");
//            return;
//        }
//
//        String symbol = parts[1];
//        String side = parts[2];
//        Double price = tryParse(parts[3]);
//        Double atr = tryParse(parts[4]);
//
//        if (price == null || atr == null ||
//                !(side.equalsIgnoreCase("buy") || side.equalsIgnoreCase("sell"))) {
//            sendMessage(chatId, "Ошибка параметров. Проверьте SIDE (buy/sell), PRICE и ATR.");
//            return;
//        }
//
//        WebhookSignalDto signal = WebhookSignalDto.builder()
//                .symbol(symbol)
//                .side(side)
//                .price(BigDecimal.valueOf(price))
//                .atr(BigDecimal.valueOf(atr))
//                .orderType("MARKET")
//                // Ниже помаенять ИД
//                .accountId(UUID.fromString("demo-account"))
//                .build();
//
//        String result = tradeController.openTrade(chatId, signal);
//        sendMessage(chatId, result);
//    }
//
//    private Double tryParse(String val) {
//        try { return Double.parseDouble(val); }
//        catch (Exception e) { return null; }
//    }
//
//    private void sendMessage(Long chatId, String text) {
//        if (sender == null) return;
//
//        SendMessage message = SendMessage.builder()
//                .chatId(chatId.toString())
//                .text(text)
//                .build();
//
//        try {
//            sender.execute(message);
//        } catch (TelegramApiException e) {
//            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
//        }
//    }
//}
