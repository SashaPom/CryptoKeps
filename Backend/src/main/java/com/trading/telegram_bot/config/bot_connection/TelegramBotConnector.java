//package com.trading.telegram_bot.config.bot_connection;
//
//import com.trading.telegram_bot.service.TelegramBotService;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//@Getter
//@AllArgsConstructor
//public class TelegramBotConnector extends TelegramLongPollingBot {
//
//    private final String token;
//    private final String botUsername;
//    private final TelegramBotService telegramBotService;
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        telegramBotService.handleUpdate(update);
//    }
//}
