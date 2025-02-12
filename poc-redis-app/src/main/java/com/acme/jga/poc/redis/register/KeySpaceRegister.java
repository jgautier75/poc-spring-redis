package com.acme.jga.poc.redis.register;

import com.acme.jga.poc.redis.config.AppRedisConfig;
import com.acme.jga.poc.redis.listener.KeyExpirationListener;
import com.acme.jga.poc.redis.utils.BeanContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class KeySpaceRegister implements InitializingBean {

    public static final String REDIS_EXPIRATION_EVT_PATTERN = "__keyevent@*:expired";

    @Autowired
    private AppRedisConfig appRedisConfig;

    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Autowired
    private BeanContextUtils beanContextUtils;

    @Autowired
    private KeyExpirationListener clusterExpirationListener;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<RedisClusterNode> masterNodes = StreamSupport.stream(lettuceConnectionFactory.getClusterConnection().clusterGetNodes().spliterator(), false).filter(RedisClusterNode::isMaster).toList();
        masterNodes.forEach(masterNode -> {
            log.info("Master Node host [{}], port [{}]", masterNode.getHost(), masterNode.getPort());
            String containerBeanName = "messageContainer" + masterNode.hashCode();
            if (beanContextUtils.containsBean(containerBeanName)) {
                return;
            }
            checkSubscribe(masterNode, containerBeanName);
        });
    }

    private void checkSubscribe(RedisClusterNode clusterNode, String containerBeanName) {
        log.info("Node [{}], subscribing to topic [{}]", clusterNode.getId(), REDIS_EXPIRATION_EVT_PATTERN);

        // Create a new RedisMessageListenerContainer on the fly for the target master node
        BeanDefinitionBuilder containerBeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RedisMessageListenerContainer.class);
        containerBeanDefinitionBuilder.addPropertyValue("connectionFactory", lettuceConnectionFactory);
        containerBeanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        containerBeanDefinitionBuilder.setLazyInit(false);
        beanContextUtils.registerBeanDefinition(containerBeanName, containerBeanDefinitionBuilder.getBeanDefinition());

        // Register message listener with targeted container
        RedisMessageListenerContainer container = beanContextUtils.findRedisMessageListenerContainer(containerBeanName);
        container.addMessageListener(clusterExpirationListener, new PatternTopic(REDIS_EXPIRATION_EVT_PATTERN));
        container.start();
    }

}
