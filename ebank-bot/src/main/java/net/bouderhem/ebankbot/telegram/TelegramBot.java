package net.bouderhem.ebankbot.telegram;

import jakarta.annotation.PostConstruct;
import net.bouderhem.ebankbot.agents.EbankAIAgent;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String telegramBotToken;
    private final EbankAIAgent aiAgent;

    public TelegramBot(@Value("${telegram.token}") String telegramBotToken,
                       EbankAIAgent aiAgent) {
        super(telegramBotToken);
        this.telegramBotToken = telegramBotToken;
        this.aiAgent = aiAgent;
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
    public void onUpdateReceived(Update update) {
        try {
            if (!update.hasMessage()) return;

            var message = update.getMessage();
            Long chatId = message.getChatId();
            String messageText = message.getText();
            String caption = message.getCaption();

            List<Media> mediaList = new ArrayList<>();
            if (message.hasPhoto()) {
                // Telegram envoie plusieurs tailles : on prend la plus grande (la dernière)
                List<PhotoSize> photos = message.getPhoto();
                PhotoSize best = photos.get(photos.size() - 1);

                GetFile getFile = new GetFile();
                getFile.setFileId(best.getFileId());
                File file = execute(getFile);

                String url = "https://api.telegram.org/file/bot" + getBotToken()
                        + "/" + file.getFilePath();
                mediaList.add(Media.builder()
                        .id(best.getFileId())
                        .mimeType(MimeTypeUtils.IMAGE_JPEG)
                        .data(new UrlResource(URI.create(url)))
                        .build());
            }

            String query = messageText != null ? messageText
                    : (caption != null ? caption : "What do you see in this image?");

            UserMessage userMessage = UserMessage.builder()
                    .text(query)
                    .media(mediaList)
                    .build();

            sendTypingAction(chatId);
            String answer = aiAgent.chat(new Prompt(userMessage), String.valueOf(chatId));
            sendTextMessage(chatId, answer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "Ebank_bot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        execute(new SendMessage(String.valueOf(chatId), text));
    }

    private void sendTypingAction(long chatId) throws TelegramApiException {
        SendChatAction chatAction = new SendChatAction();
        chatAction.setChatId(String.valueOf(chatId));
        chatAction.setAction(ActionType.TYPING);
        execute(chatAction);
    }
}