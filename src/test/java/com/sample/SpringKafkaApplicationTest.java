package com.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import com.sample.consumer.Receiver;
import com.sample.model.Product;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.sample.producer.Sender;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringKafkaApplicationTest {

  @Autowired
  private Sender sender;

  @Autowired
  private Receiver receiver;

  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @ClassRule
  public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, "product");

  @Before
  public void setUp() throws Exception {
    // wait until the partitions are assigned
    for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
        .getListenerContainers()) {
      ContainerTestUtils.waitForAssignment(messageListenerContainer,
          embeddedKafka.getPartitionsPerTopic());
    }
  }

  @Test
  public void testReceive() throws Exception {
    Product product = new Product("Camera", "GoPro");
    sender.sendToFailedTopic(product);

    receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    assertThat(receiver.getLatch().getCount()).isEqualTo(0);
  }
}
