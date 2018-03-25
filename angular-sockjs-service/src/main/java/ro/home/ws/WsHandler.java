package ro.home.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import ro.home.api.model.ChatMessage;

@Controller
final class WsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsHandler.class);

    @MessageMapping("/chat")
    @SendTo("/topic/chatMessages")
    public ChatMessage send(ChatMessage message) {
        LOGGER.info("Mesaj: {}" , message.toString());
        return message;
    }
}
