package lr.Messages;

/**
 * Created by luca on 02/03/16.
 */

import lr.ConsistentHash;
import lr.Data;
import lr.Node;

import java.util.Map;
import java.util.TreeMap;

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

public class MessageStatus extends Message {

    private Map<String, Data<?>> store;
    private TreeMap<Long, Node> ch;

    public TreeMap<Long, Node> getCh() {
        return ch;
    }

    public void setCh(TreeMap<Long, Node> ch) {
        this.ch = ch;
    }

    public MessageStatus() {
    }

    public MessageStatus(Node sender, Map<String, Data<?>> store, TreeMap<Long, Node> ch) {
        super(sender);
        this.store = store;
        this.ch = ch;
    }

    public MessageStatus(Node sender) {
        this(sender, null, null);
    }

    public Map<String, Data<?>> getStore() {
        return store;
    }

    public void setStore(Map<String, Data<?>> store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "MessageStatus{" +
                "store=" + store +
                "} " + super.toString();
    }
}
