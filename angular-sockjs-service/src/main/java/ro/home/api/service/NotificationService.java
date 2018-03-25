package ro.home.api.service;


import ro.home.api.model.ChatMessage;

public interface NotificationService {

    void broadcast(String destination, Object message);

    void broadcastChatMessage(ChatMessage message);
}
