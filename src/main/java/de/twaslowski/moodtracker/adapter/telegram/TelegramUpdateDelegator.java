package de.twaslowski.moodtracker.adapter.telegram;

import de.twaslowski.moodtracker.adapter.telegram.dto.response.TelegramResponse;
import de.twaslowski.moodtracker.adapter.telegram.dto.update.TelegramUpdate;
import de.twaslowski.moodtracker.adapter.telegram.handler.UpdateHandler;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramUpdateDelegator {

  private final Collection<UpdateHandler> handlers;

  public TelegramResponse delegateUpdate(TelegramUpdate update) {
    var relevantHandler = handlers.stream()
        .filter(handler -> handler.canHandle(update))
        .findFirst();

    return relevantHandler
        .map(handler -> invokeHandler(handler, update))
        .orElseGet(() -> {
          log.warn("No handler found for update {}", update);
          return respondToUnhandleableUpdate(update.getChatId());
        });
  }

  private TelegramResponse invokeHandler(UpdateHandler handler, TelegramUpdate update) {
    try {
      return handler.handleUpdate(update);
    } catch (Exception e) {
      log.error("Error while processing update: {}", e.getMessage());
      return TelegramResponse.error()
          .chatId(update.getChatId())
          .build();
    }
  }

  private TelegramResponse respondToUnhandleableUpdate(long chatId) {
    return TelegramResponse.unhandleableUpdate()
        .chatId(chatId)
        .build();
  }
}
