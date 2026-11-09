package org.lahlou.net.ebankbot.telegram;

import org.lahlou.net.ebankbot.agents.EbankAIAgent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.token}")
    private String telegramBotToken;

    private final EbankAIAgent ebankAIAgent;

    public TelegramBot(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update telegramRequest) {
        try {
            if (!telegramRequest.hasMessage()) return;

            String messageText = telegramRequest.getMessage().getText();
            Long chatId = telegramRequest.getMessage().getChatId();
            List<PhotoSize> photos = telegramRequest.getMessage().getPhoto();
            List<Media> mediaList = new ArrayList<>();
            String caption = null;

            if (photos != null && !photos.isEmpty()) {
                caption = telegramRequest.getMessage().getCaption();
                if (caption == null) caption = "What do you see in this image";

                // Récupérer la photo de la plus haute qualité (la dernière de la liste)
                PhotoSize ps = photos.get(photos.size() - 1);
                String fileId = ps.getFileId();
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                File file = execute(getFile);
                String filePath = file.getFilePath();

                String textUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
                URL fileUrl = new URL(textUrl);

                mediaList.add(new Media(MimeTypeUtils.IMAGE_PNG, new UrlResource(fileUrl)));
            }

            String query = messageText != null ? messageText : caption;
            UserMessage.Builder userMessageBuilder = UserMessage.builder()
                    .text(query != null ? query : "Analyse cette image.");
            if (!mediaList.isEmpty()) {
                for (Media media : mediaList) {
                    userMessageBuilder.media(media);
                }
            }

            UserMessage userMessage = userMessageBuilder.build();
            sendTypingQuestion(chatId);
            String answer = ebankAIAgent.chat(new Prompt(userMessage));
            sendTextMessage(chatId, answer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "EMSIAIBot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        execute(sendMessage);
    }

    private void sendTypingQuestion(long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        execute(sendChatAction);
    }
}