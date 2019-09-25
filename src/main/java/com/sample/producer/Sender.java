package com.sample.producer;

import com.sample.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;


public class Sender {

  private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

  @Value("${kafka.retry_topic.json}")
  private String jsonTopic;

  @Autowired
  private KafkaTemplate<String, Product> kafkaTemplate;

  @Async
  public void sendToFailedTopic(Product product) {
    LOGGER.info("sending failed saved product='{}'", product.toString());
    kafkaTemplate.send(jsonTopic, product);
  }
}
