package com.cpd.proiect.subscribe;

import com.cpd.proiect.gui.SubMainPanel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionsListener {

    public static final String SUBSCRIPTION_QUEUE_1 = "cpd";
    public static final String SUBSCRIPTION_QUEUE_2 = "licenta";

    @RabbitListener(queues = SUBSCRIPTION_QUEUE_1)
    public void firstQueueListener(Message message){
        String stringMessage = new String(message.getBody());
        SubMainPanel.updateSubContent1(stringMessage);
//        System.out.println("Din " + SUBSCRIPTION_QUEUE_1 + " queue am primit: " + stringMessage);
    }

    @RabbitListener(queues = SUBSCRIPTION_QUEUE_2)
    public void secondQueueListener(Message message){
        String stringMessage = new String(message.getBody());
        SubMainPanel.updateSubContent2(stringMessage);
//        System.out.println("Din " + SUBSCRIPTION_QUEUE_2 + " queue am primit: " + stringMessage);
    }



}
