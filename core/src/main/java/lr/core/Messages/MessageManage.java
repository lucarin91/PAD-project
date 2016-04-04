package lr.core.Messages;

/**
 * Created by luca on 02/03/16.
 */

import lr.core.Data;
import lr.core.Nodes.Node;

public class MessageManage extends Message{
    private MSG_OPERATION operation;
    private String key;
    private Data<?> data;

    public MessageManage(){ }

    public MessageManage(Node sender, MSG_OPERATION operation, Data<?> data) {
        super(sender);
        this.operation = operation;
        this.data = data;
        this.key = data.getKey();
    }

    public MessageManage(Node sender, MSG_OPERATION operation, String key) {
        super(sender);
        this.operation = operation;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MSG_OPERATION getOperation() {
        return operation;
    }

    public void setOperation(MSG_OPERATION operation) {
        this.operation = operation;
    }

    public void setData(Data<?> data) {
        this.data = data;
    }

    public Data<?> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MessageManage{" +
                "operation=" + operation +
                ", key='" + key + '\'' +
                ", data=" + data +
                "} " + super.toString();
    }
}
