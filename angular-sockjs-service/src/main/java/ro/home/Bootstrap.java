package ro.home;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import ro.home.config.RestConfig;
import ro.home.config.WebSocketConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class Bootstrap implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {

        AnnotationConfigWebApplicationContext restContext = new AnnotationConfigWebApplicationContext();
        restContext.register(RestConfig.class);
        DispatcherServlet restServlet = new DispatcherServlet(restContext);

        ServletRegistration.Dynamic restDispatcher = container.addServlet("restDispatcher", restServlet);
        restDispatcher.addMapping("/rest/*");
        restDispatcher.setLoadOnStartup(1);
        restDispatcher.setAsyncSupported(true);


        AnnotationConfigWebApplicationContext webSocketContext = new AnnotationConfigWebApplicationContext();
        webSocketContext.register(WebSocketConfig.class);
        DispatcherServlet webSocketServlet = new DispatcherServlet(webSocketContext);

        ServletRegistration.Dynamic webSocketDispatcher = container.addServlet("webSocketDispatcher", webSocketServlet);
        webSocketDispatcher.addMapping("/ws/*");
        webSocketDispatcher.setLoadOnStartup(2);
        webSocketDispatcher.setAsyncSupported(true);
    }
}
