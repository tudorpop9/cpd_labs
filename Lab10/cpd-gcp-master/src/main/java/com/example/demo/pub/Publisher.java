package com.example.demo.pub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

  @Autowired
  PubsubOutboundGateway gateway;

  private static Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

  @Scheduled(fixedDelay = 5000)
  public void keepAlive() {
    gateway.sendToPubsub("news on finance");
    LOGGER.info("keep alive");
  }

}
