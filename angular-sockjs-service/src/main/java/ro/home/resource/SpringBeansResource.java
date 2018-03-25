package ro.home.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/springBeans")
final class SpringBeansResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBeansResource.class);

    @Autowired
    private ApplicationContext context;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Map<String, String[]> getSpringBeans() {
        Set<String> beanTypes = new HashSet<>();
        Map<String, String[]> springBeans = new TreeMap<>();
        for (String bean : context.getBeanDefinitionNames()) {
            beanTypes.add(context.getType(bean).getName());
        }
        beanTypes.forEach(beanType -> {
            try {
                Class beanClass = Class.forName(beanType);
                springBeans.put(beanType, context.getBeanNamesForType(beanClass));
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return springBeans;
    }
}
