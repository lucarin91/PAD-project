package lr;

/**
 * Created by luca on 02/03/16.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

enum MSG_TYPE {MANAGEMENT, REQUEST, RESPONSE}
enum MSG_OPERATION {ADD,DEL,UP,GET, STATUS}

public class Message {
    private Node sender;
    private MSG_TYPE type;
    private MSG_OPERATION operation;

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

