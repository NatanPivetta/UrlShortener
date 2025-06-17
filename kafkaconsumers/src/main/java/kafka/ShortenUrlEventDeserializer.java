package kafka;


import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ShortenUrlEventDeserializer extends ObjectMapperDeserializer<ShortenUrlEvent> {
    public ShortenUrlEventDeserializer() {super(ShortenUrlEvent.class);}
}
