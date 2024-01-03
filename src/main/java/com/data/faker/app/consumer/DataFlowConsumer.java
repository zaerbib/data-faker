package com.data.faker.app.consumer;

import com.data.faker.app.document.DataFlow;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("activemq")
@EnableJms
public class DataFlowConsumer {

    private final MongoTemplate mongoTemplate;

    @JmsListener(destination = "QUEUE_TOPIC")
    public void receiveMessage(DataFlow message) {
        try {
            log.debug("Received message from activeMQ : {}", message);
            mongoTemplate.save(message);
        } catch (Exception e) {
            log.error("Unknown Error occured in processing Message", e);
            throw new RuntimeException("Problem in receiving message from  Active MQ");
        }
    }
}
