package ro.home.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.home.api.model.ChatMessage;
import ro.home.api.model.InformationMessage;
import ro.home.api.service.NotificationService;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/notifications")
final class NotificationResource {

    @Autowired
    @Lazy
    private NotificationService notificationService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public InformationMessage handleMessage(@RequestBody ChatMessage message) {
        notificationService.broadcastChatMessage(message);
        return new InformationMessage("ChatMessage " + message.getMessage() + " has been posted.");
    }
}
