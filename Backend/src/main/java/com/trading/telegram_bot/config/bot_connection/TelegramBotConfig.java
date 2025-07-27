//package com.trading.telegram_bot.config.bot_connection;
//
//import com.trading.telegram_bot.service.TelegramBotService;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Slf4j
//@Configuration
//public class TelegramBotConfig {
//
//    @Value("${telegram.token}")
//    private String token;
//
//    @Value("${telegram.username}")
//    private String username;
//
//    @Autowired
//    private TelegramBotService telegramBotService;
//
//    @Autowired
//    private TelegramBotConnectorFactory connectorFactory;
//
//    @PostConstruct
//    public void init() {
//        try {
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            TelegramBotConnector connector = connectorFactory.create(token, username, telegramBotService);
//            botsApi.registerBot(connector);
//        } catch (TelegramApiException e) {
//            log.error("Error registering Telegram bot", e);
//        }
//    }
//}
//
