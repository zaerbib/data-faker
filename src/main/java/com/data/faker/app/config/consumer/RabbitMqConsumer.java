package com.data.faker.app.config.consumer;

import com.data.faker.app.document.DataFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitMq")
@Slf4j
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final MongoTemplate template;

    @RabbitListener(queues = "#{queue.name}")
    public void consume(DataFlow dataFlow) {
        try {
            log.debug("Received message from activeMQ : {}", dataFlow);
            template.save(dataFlow);
        } catch(Exception e) {
            log.error("Unknown Error occured in processing Message", e);
            throw new RuntimeException("Problem in receiving message from  Active MQ");
        }
    }
}
