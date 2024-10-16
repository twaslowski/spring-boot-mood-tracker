package de.twaslowski.moodtracker.adapter.telegram.external;

import de.twaslowski.moodtracker.adapter.telegram.dto.response.TelegramInlineKeyboardResponse;
import de.twaslowski.moodtracker.adapter.telegram.dto.response.TelegramResponse;
import de.twaslowski.moodtracker.adapter.telegram.dto.response.TelegramTextResponse;
import java.util.Comparator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

public class BotApiMessageFactory {

  public static SendMessage createBotApiMessage(TelegramResponse response) {
    return switch (response.getResponseType()) {
      case TEXT -> createTextResponse((TelegramTextResponse) response);
      case INLINE_KEYBOARD ->
          createInlineKeyboardResponse((TelegramInlineKeyboardResponse) response);
    };
  }

  private static SendMessage createTextResponse(TelegramTextResponse response) {
    return SendMessage.builder()
        .chatId(response.getChatId())
        .text(response.getMessage())
        .build();
  }

  // todo: Right now, responses are unordered. This needs to be fixed.
  private static SendMessage createInlineKeyboardResponse(TelegramInlineKeyboardResponse response) {
    var inlineKeyboardButtons = response.getContent().entrySet().stream()
        .map(entry -> new InlineKeyboardRow(InlineKeyboardButton.builder()
            .text(entry.getKey())
            .callbackData(entry.getValue())
            .build()))
        // The approach is solid, I just need a better comparator
        .sorted(Comparator.comparing(a -> a.getFirst().getCallbackData()))
        .toList();

    return SendMessage.builder()
        .chatId(response.getChatId())
        .text(response.getMessage())
        .replyMarkup(InlineKeyboardMarkup.builder()
            .keyboard(inlineKeyboardButtons)
            .build())
        .build();
  }
}
