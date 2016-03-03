package lr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import lr.Messages.Message;
import lr.Messages.MessageManage;
import lr.Messages.MessageStatus;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by luca on 03/03/16.
 */
public class JsonHelper {
    private static Optional<ObjectMapper> _messageMapper;

    private static ObjectMapper getMessageMapper() {
        return _messageMapper.orElseGet(() -> {
            ObjectMapper mapper = _messageMapper.of(new ObjectMapper()).get();
            mapper.registerSubtypes(
                    new NamedType(MessageManage.class, "MessageManage"),
                    new NamedType(MessageStatus.class, "MessageStatus")
            );
            return mapper;
        });
    }

    public static Message deserialize(String json) throws IOException {
        return getMessageMapper().readValue(json, Message.class);
    }

    public static <T> String serialize(T msg) throws JsonProcessingException {
        return getMessageMapper().writerWithType(new TypeReference<T>() {
        })
                .writeValueAsString(msg);
    }
}
