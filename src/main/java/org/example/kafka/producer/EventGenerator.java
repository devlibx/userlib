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
                sleep(100);
            }
        });
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KafkaMessagingTestConfig {
        public MessagingConfigs messaging;
    }
}
