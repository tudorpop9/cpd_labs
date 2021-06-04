package com.example.demo.sub;

import com.example.demo.config.GcpConfiguration;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class Subscriber {

  private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);

  // Define what happens to the messages arriving in the message channel.
  @ServiceActivator(inputChannel = "inputMessageChannel")
  public void messageReceiver(
          String payload,
          @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
    LOGGER.info("Message arrived via an inbound channel adapter from sub-one! Payload: " + payload);
    message.ack();
  }
}
