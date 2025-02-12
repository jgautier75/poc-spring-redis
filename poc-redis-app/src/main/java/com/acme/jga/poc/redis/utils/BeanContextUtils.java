package com.acme.jga.poc.redis.utils;

import com.acme.jga.poc.redis.listener.KeyExpirationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
public class BeanContextUtils {

    @Autowired
    private WebApplicationContext webApplicationContext;

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (webApplicationContext instanceof AnnotationConfigServletWebServerApplicationContext) {
            ((AnnotationConfigServletWebServerApplicationContext) webApplicationContext).registerBeanDefinition(beanName, beanDefinition);
        }
    }

    public KeyExpirationListener findKeyExpirationListener(String beanName) {
        return webApplicationContext.getBean(beanName, KeyExpirationListener.class);
    }

    public boolean containsBean(String beanName) {
        return webApplicationContext.containsBean(beanName);
    }

    public RedisMessageListenerContainer findRedisMessageListenerContainer(String beanName) {
        return webApplicationContext.getBean(beanName, RedisMessageListenerContainer.class);
    }
}
