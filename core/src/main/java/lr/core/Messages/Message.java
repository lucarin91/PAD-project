package lr.core.Messages;

/**
 * Created by luca on 02/03/16.
 */

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lr.core.Nodes.Node;

/***
 * Type of message:
 * <p>
 * GET
 * {"type": "GET", "sender_type": FRONT|BACK, "key": ..., "hash": ... }
 * <p>
 * ADD
 * {"type": "ADD", "sender_type": FRONT|BACK, "key": ..., "hash": ..., "data": ....}
 * UP
 * {"type": "UP", "sender_type": FRONT|BACK, "key": ..., "hash": ..., "data": ....}
 * <p>
 * DEL
 * {"type": "DEL", "sender_type": FRONT|BACK, "key": ...., "hash": ... }
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageManage.class, name = "msg_manage"),
        @JsonSubTypes.Type(value = MessageRequest.class, name = "msg_request"),
        @JsonSubTypes.Type(value = MessageResponse.class, name = "msg_response"),
        @JsonSubTypes.Type(value = MessageStatus.class, name = "msg_status")})
public class Message {
    public enum MSG_OPERATION {ADD, DELETE, UPDATE, GET, ADDorUPDATE, STATUS}

    private Node sender;
    public Message() { }

    public Message(Node sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                '}';
    }

    public Node getSender() {
        return sender;
    }

    public void setSender(Node sender) {
        this.sender = sender;
    }
}

