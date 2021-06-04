package com.cpd.proiect.publish;

import com.cpd.proiect.control.FirstPublishConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FirstPublisher {
    @Autowired
    private RabbitTemplate template;

    public synchronized void publish(String message){
        template.convertAndSend(FirstPublishConfig.EXCHANGE,
                FirstPublishConfig.PUB_ROUTING_KEY_1, message);
    }
}
