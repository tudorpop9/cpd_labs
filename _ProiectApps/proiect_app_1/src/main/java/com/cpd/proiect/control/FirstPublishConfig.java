package com.cpd.proiect.control;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FirstPublishConfig {

    public static final String EXCHANGE = "spring-boot-exchange";
    public static final String PUB_QUEUE_1 = "cpd";
    public static final String PUB_ROUTING_KEY_1 = "key_cpd";

    @Bean
    public Queue queue1(){
        return new Queue(PUB_QUEUE_1, true);
    }

    @Bean
    public TopicExchange exchange1(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding1(Queue queue1, TopicExchange exchange1){
        return BindingBuilder.bind(queue1).to(exchange1).with(PUB_ROUTING_KEY_1);
    }

    @Bean
    public MessageConverter converterToJSON1(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converterToJSON1());
        return rabbitTemplate;
    }

}
