package com.cpd.proiect.control;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SecondPublishConfig {

    public static final String EXCHANGE = "spring-boot-exchange";
    public static final String PUB_QUEUE_2 = "licenta";
    public static final String PUB_ROUTING_KEY_2 = "key_licenta";

    @Bean
    public Queue queue2(){
        return new Queue(PUB_QUEUE_2, true);
    }

    @Bean
    public TopicExchange exchange2(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding2(Queue queue2, TopicExchange exchange2){
        return BindingBuilder.bind(queue2).to(exchange2).with(PUB_ROUTING_KEY_2);
    }

    @Bean
    public MessageConverter converterToJSON2(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template2(ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converterToJSON2());
        return rabbitTemplate;
    }


}
