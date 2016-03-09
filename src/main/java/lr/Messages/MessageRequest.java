package lr.Messages;

import lr.Node;

/**
 * Created by luca on 07/03/16.
 */
public class MessageRequest<T> extends Message {
    private String key;
    private T value;
    private MSG_OPERATION operation;

    public MessageRequest(){ }

    public MessageRequest(Node sender, MSG_OPERATION operation, String key, T value) {
        super(sender);
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public MessageRequest(Node sender, MSG_OPERATION operation, String key) {
        super(sender);
        this.key = key;
        this.operation = operation;
    }

    public MessageRequest(Node sender, MSG_OPERATION operation) {
        super(sender);
        this.operation = operation;
    }

    public MessageRequest(MSG_OPERATION operation, String key, T value) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public MessageRequest(MSG_OPERATION operation, String key) {
        this.key = key;
        this.operation = operation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public MSG_OPERATION getOperation() {
        return operation;
    }

    public void setOperation(MSG_OPERATION operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", operation=" + operation +
                "} " + super.toString();
    }
}
