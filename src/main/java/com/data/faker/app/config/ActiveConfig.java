package com.data.faker.app.config;

import com.data.faker.app.document.DataFlow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Profile("activemq")
@Configuration
public class ActiveConfig {
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    @Value("${spring.activemq.user}")
    private String username;
    @Value("${spring.activemq.password}")
    private String password;

    private final ObjectMapper objectMapper;

    public ActiveConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setTrustedPackages(Collections.singletonList(DataFlow.class.getPackageName()));

        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory
                    defaultJmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory());
        factory.setMessageConverter(messageConverter(objectMapper));
        return null;
    }

    @Bean
    public MappingJackson2MessageConverter messageConverter(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("message_type");
        converter.setObjectMapper(objectMapper);

        Map<String, Class<?>> typeIdMap = new HashMap<>();
        typeIdMap.put("dataFlow", DataFlow.class);
        converter.setTypeIdMappings(typeIdMap);

        return converter;
    }
}
