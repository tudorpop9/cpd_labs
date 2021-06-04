package com.example.demo.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class GcpConfiguration {

  // Create a message channel for messages arriving from the subscription `sub-one`.
  @Bean
  public MessageChannel inputMessageChannel() {
    return new PublishSubscribeChannel();
  }

  // Create an inbound channel adapter to listen to the subscription `sub-one` and send
  // messages to the input message channel.
  @Bean
  public PubSubInboundChannelAdapter inboundChannelAdapter(
          @Qualifier("inputMessageChannel") MessageChannel messageChannel,
          PubSubTemplate pubSubTemplate) {
    PubSubInboundChannelAdapter adapter =
            new PubSubInboundChannelAdapter(pubSubTemplate, "finance-biziday");
    adapter.setOutputChannel(messageChannel);
    adapter.setAckMode(AckMode.MANUAL);
    adapter.setPayloadType(String.class);
    return adapter;
  }

  @Bean
  public MessageChannel pubsubOutputChannel() {
    return new PublishSubscribeChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "pubsubOutputChannel")
  public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
    return new PubSubMessageHandler(pubsubTemplate, "finance");
  }

}
