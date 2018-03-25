package ro.home.internal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import ro.home.api.model.ChatMessage;
import ro.home.api.service.NotificationService;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import static java.util.Objects.nonNull;

import static org.springframework.web.servlet.FrameworkServlet.SERVLET_CONTEXT_PREFIX;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

@Service
@Lazy
final class DefaultNotificationService implements NotificationService, ServletContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultNotificationService.class);
    private ServletContext servletContext;
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    private void setBrokerMessagingTemplate() {
        String targetServlet = SERVLET_CONTEXT_PREFIX + "webSocketDispatcher";
        messagingTemplate = getWebApplicationContext(servletContext, targetServlet).getBean(SimpMessagingTemplate.class);
    }

    @Override
    public void broadcast(String destination, Object message) {
        if (nonNull(destination) && nonNull(message)) {
            messagingTemplate.convertAndSend(destination, message);
            LOGGER.info(" Message {} has been sent ! ", message.toString());
        }
    }

    @Override
    public void broadcastChatMessage(ChatMessage message) {
        if (nonNull(message)) {
            messagingTemplate.convertAndSend("/topic/chatMessages", message);
            LOGGER.info(" Message {} has been sent ! ", message.toString());
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
