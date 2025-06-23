package kafka;


import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import model.User;

public class UserEventDeserializer extends ObjectMapperDeserializer<UserRegisterEvent> {
    public UserEventDeserializer() {
        super(UserRegisterEvent.class);
    }
}
