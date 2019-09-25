package com.sample.consumer;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.constants.ColorConstant;
import com.sample.model.Product;
import com.sample.producer.Sender;
import com.sample.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;

public class Receiver {

  @Autowired
  private ProductService productService;

  @Autowired
  private Sender sender;

  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

  private CountDownLatch latch = new CountDownLatch(1);

  public CountDownLatch getLatch() {
    return latch;
  }

  @KafkaListener(topics = "${kafka.topic.json}", containerFactory = "kafkaListenerContainerFactory")
  public void receive(String product) throws Exception {
    Product product1 = jsonStringToObject(product);
    LOGGER.info(ColorConstant.ANSI_BLUE+"Received Product='{}'", product1.toString());
  try {
    LOGGER.info(ColorConstant.ANSI_CYAN+"Saving: " + product1.toString() + " to database...");
    productService.saveToDatabase(product1);

  } catch (SQLException ex) {
    sender.sendToFailedTopic(product1);
  }
    latch.countDown();
  }

  @KafkaListener(topics = "${kafka.retry_topic.json}", containerFactory = "retrykafkaListenerContainerFactory")
  public void receiveFailed(String product) throws Exception {
    Product product1 = jsonStringToObject(product);
    LOGGER.info(ColorConstant.ANSI_GREEN+"Received Failed Product='{}'", product1.toString());
    LOGGER.info(ColorConstant.ANSI_GREEN+"Retry Saving: " + product1.toString() + " to database...");
    productService.saveToDatabase(product1);
  }

  public Product jsonStringToObject(String data) {
    ObjectMapper mapper = new ObjectMapper();
    Product object = null;
    try {
      object = mapper.readValue(data, Product.class);
    } catch (Exception exception) {
      System.out.println("Error in deserializing bytes "+ exception);
    }
    return object;
  }
}
