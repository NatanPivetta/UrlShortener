package kafka;


import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class AccessEventDeserializer extends ObjectMapperDeserializer<AccessEvent> {
    public AccessEventDeserializer() {
        super(AccessEvent.class);
    }
}
