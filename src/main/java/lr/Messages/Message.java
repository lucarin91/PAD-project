package lr.Messages;

/**
 * Created by luca on 02/03/16.
 */

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lr.Node;

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
        @JsonSubTypes.Type(value = MessageStatus.class, name = "msg_status")})
public class Message {
    public enum MSG_TYPE {MANAGEMENT, REQUEST, RESPONSE, STATUS}
    public enum MSG_OPERATION {ADD,DEL,UP,GET}

    private Node sender;
    private MSG_TYPE type;
    private MSG_OPERATION operation;

    public Message() { }

    public Message(MSG_TYPE type, MSG_OPERATION operation, Node sender) {
        this.type = type;
        this.operation = operation;
        this.sender = sender;
    }

    public Message(MSG_TYPE type, Node sender) {
        this.type = type;
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", type=" + type +
                ", operation=" + operation +
                '}';
    }

    public MSG_OPERATION getOperation() {
        return operation;
    }

    public void setOperation(MSG_OPERATION operation) {
        this.operation = operation;
    }

    public MSG_TYPE getType() {
        return type;
    }

    public void setType(MSG_TYPE type) {
        this.type = type;
    }

    public Node getSender() {
        return sender;
    }

    public void setSender(Node sender) {
        this.sender = sender;
    }
}

