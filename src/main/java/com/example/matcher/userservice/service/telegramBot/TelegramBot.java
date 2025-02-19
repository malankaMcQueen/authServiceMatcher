package com.example.matcher.userservice.service.telegramBot;

import com.example.matcher.userservice.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;


//    private final UserService userService; // Ваш сервис для работы с пользователями

    @Override
    public String getBotUsername() {
        System.out.println("Bot Username: " + botUsername); // Проверка токена
        return botUsername;
    }
    @PostConstruct
    public void checkBotCredentials() {
        System.out.println("Bot Username from Spring: " + botUsername);
        System.out.println("Bot Token from Spring: " + botToken);
    }

    @Override
    public String getBotToken() {
        logger.info("Bot token: " + botToken); // Проверка токена
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleIncomingMessage(update.getMessage());
        }
    }

    private void handleIncomingMessage(Message message) {
        String chatId = message.getChatId().toString();
        String text = message.getText();
        if ("/start".equals(text)) {
            handleStartCommand(message);
        } else {
            sendMessage(chatId, "Я пока не понимаю эту команду.");
        }
    }

    private void handleStartCommand(Message message) {
        Long userId = message.getFrom().getId();

        String token = authenticationService.getTelegramAuthToken(userId);
        String authUrl = "http://localhost:8080/UserService/auth/telegram/authorize?token=" + token;

        String messageText = "Нажмите на ссылку для авторизации: " + authUrl;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(messageText);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            sendMessage(message.getChatId().toString(), "Some error");
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }
}


