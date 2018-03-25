package ro.home.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Import(RootConfig.class)
@ComponentScan(basePackages = "ro.home.resource")
@EnableWebMvc
public class RestConfig {

    /*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/js/");
    }
    */
}
