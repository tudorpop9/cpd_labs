package com.cpd.proiect.publish;

import com.cpd.proiect.control.FirstPublishConfig;
import com.cpd.proiect.control.SecondPublishConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecondPublisher {
    @Autowired
    private RabbitTemplate template;

    public synchronized void publish(String message){
        template.convertAndSend(SecondPublishConfig.EXCHANGE,
                SecondPublishConfig.PUB_ROUTING_KEY_2, message);
    }
}
