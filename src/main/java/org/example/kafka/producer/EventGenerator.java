package org.example.kafka.producer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.gitbub.devlibx.easy.helper.common.LogEvent;
import io.gitbub.devlibx.easy.helper.json.JsonUtils;
import io.gitbub.devlibx.easy.helper.yaml.YamlUtils;
import io.github.devlibx.easy.messaging.config.MessagingConfigs;
import io.github.devlibx.easy.messaging.kafka.module.MessagingKafkaModule;
import io.github.devlibx.easy.messaging.module.MessagingModule;
import io.github.devlibx.easy.messaging.service.IMessagingFactory;
import lombok.Data;

import java.util.Random;
import java.util.UUID;

public class EventGenerator {

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    public static void main(String[] args) {

        // Read config from file
        final KafkaMessagingTestConfig kafkaConfig = YamlUtils.readYaml("app.yaml", KafkaMessagingTestConfig.class);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MessagingConfigs.class).toInstance(kafkaConfig.messaging);
            }
        }, new MessagingKafkaModule(), new MessagingModule());

        // Get messaging factory and initialize the factory
        IMessagingFactory messagingFactory = injector.getInstance(IMessagingFactory.class);
        messagingFactory.initialize();


        LogEvent.setGlobalServiceName("example");

        // @JsonProperty("rowNumber")
        //    private String rowNumber;
        //    @JsonProperty("orderKey")
        //    private long orderKey;
        //    @JsonProperty("customerKey")
        //    private long customerKey;
        //    @JsonProperty("orderStatus")
        //    private String orderStatus;
        //    @JsonProperty("totalPrice")
        //    private float totalPrice;
        //    @JsonProperty("orderDate")
        //    private String orderDate;
        //    @JsonProperty("orderPriority")
        //    private String orderPriority;
        //    @JsonProperty("clerk")
        //    private String clerk;
        //    @JsonProperty("shipPriority")
        //    private long shipPriority;
        //    @JsonProperty("comment")
        //    private String comment;

        if (false) {
            String comment = "ABCDEFGH";
            String comment1 = Strings.repeat(comment, 1);

            for (int j = 0; j < 1; j++) {
                int f = j;
                new Thread(() -> {
                    int offset = f;
                    messagingFactory.getProducer("metric").ifPresent(producer -> {
                        for (int i = 0; i < 1_000_000; i++) {
                            Order order = new Order();
                            order.setOrderKey((offset * 1_000_000) + i);
                            order.setCustomerKey((offset * 1_000_100) + i + 10);
                            order.setRowNumber(UUID.randomUUID().toString());
                            order.setOrderDate("F");
                            order.setOrderStatus("F");
                            order.setTotalPrice(10);
                            order.setOrderDate(UUID.randomUUID().toString());
                            order.setClerk(UUID.randomUUID().toString());
                            order.setShipPriority(1);
                            order.setComment(comment1);
                            order.setTimestamp(System.currentTimeMillis());
                            producer.send(UUID.randomUUID().toString(), JsonUtils.asJson(order).getBytes());
                            sleep(1000);
                        }

                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            if (true) {
                return;
            }
        }


        if (false) {
            messagingFactory.getProducer("metric").ifPresent(producer -> {
                LogEvent event = LogEvent.Builder
                        .withEventTypeAndEntity("sent", "user", UUID.randomUUID().toString())
                        .data("name", "harish4", "status", "F", "last_name", "bohara", "count1", 102, "count", 13, "timestamp", System.currentTimeMillis())
                        .build();

                producer.send(UUID.randomUUID().toString(), JsonUtils.asJson(event.getData()).getBytes());
            });
            return;
        }

        messagingFactory.getProducer("metric").ifPresent(producer -> {
            Random r = new Random();

            while (true) {
                if (true) break;
                LogEvent event = LogEvent.Builder
                        .withEventTypeAndEntity("sent", "user", UUID.randomUUID().toString())
                        // .data("name", "harish5", "status", "M", "last_name", "bohara_1", "count1", 1021, "count", 199, "timestamp", System.currentTimeMillis())
                        // .data("name", "harish5", "last_name", "only_last_name_should_update" , "timestamp", System.currentTimeMillis())
                        // .data("name", "harish5", "count", 18 , "count1", 1021,  "last_name", "only_last_name_should_update" ,"timestamp", System.currentTimeMillis())
                        .data("name", "harish6", "count1", 10211, "last_name", "only_last_name_should_update", "timestamp", System.currentTimeMillis())
                        .build();

                producer.send(UUID.randomUUID().toString(), JsonUtils.asJson(event).getBytes());
                // if (true) return;

                double val = r.nextGaussian() * 50 + 200;
                sleep((int) val);
                sleep(1000);
            }
        });


        messagingFactory.getProducer("metric").ifPresent(producer -> {
            Random random = new Random();
            int count = 0;
            while (true) {
                int id = random.nextInt(100_000_000);
                LogEvent event = LogEvent.Builder
                        .withEventTypeAndEntity("sent", "user", "id_" + id)
                        .dimensions("event_name", "sender", "type", "clear")
                        .data("name", "harish_" + random.nextInt(100_000_000), "user_id", "uid_" + id, "count1", random.nextInt(100_000_000), "last_name", "only_last_name_should_update", "timestamp", System.currentTimeMillis())
                        .data("udf_long_2", random.nextInt(1_000_000), "udf_str_2", "bohara_" + random.nextInt(10_000_000), "udf_long_1", random.nextInt(10_000_000))
                        .build();
                producer.send(UUID.randomUUID().toString(), JsonUtils.asJson(event).getBytes());
                count++;
                System.out.println("-->> " + count);
                sleep(1000);
            }
        });
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KafkaMessagingTestConfig {
        public MessagingConfigs messaging;
    }
}
