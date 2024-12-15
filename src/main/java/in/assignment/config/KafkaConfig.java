package in.assignment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic sourceTopic() {
        return new NewTopic("SOURCE_TOPIC", 1, (short) 1);
    }

    @Bean
    public NewTopic evenTopic() {
        return new NewTopic("EVEN_TOPIC", 1, (short) 1);
    }

    @Bean
    public NewTopic oddTopic() {
        return new NewTopic("ODD_TOPIC", 1, (short) 1);
    }
}
