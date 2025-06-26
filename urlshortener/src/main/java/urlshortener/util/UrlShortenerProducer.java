package urlshortener.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

@ApplicationScoped
public class UrlShortenerProducer {

    ObjectMapper mapper = new ObjectMapper();

    KafkaProducer<String, String> producer;

    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "kafka:9093")
    String bootstrapServers;


    @PostConstruct
    public void init() {
        System.out.println(bootstrapServers);
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9093");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        producer = new KafkaProducer<>(props);
    }

    public void send(String topic, String event){
        producer.send(new ProducerRecord<>(topic, event));
    }
}
