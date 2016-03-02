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

public class Message {

    public enum MSG_TYPE {ADD, DEL, UP, GET, STATUS}

    public enum SENDER_TYPE {FRONT, MASTER, BACK}

    private MSG_TYPE type;
    private SENDER_TYPE sender_type;
    private Node sender;
    private Data<?> data;
    private Map<String, Data<?>> store;
    //private String sender_ip;
    //private int sender_port;

    public Message(JSONObject json) {
        try {
            this.type = MSG_TYPE.valueOf(json.getString("type"));
            this.sender_type = SENDER_TYPE.valueOf(json.getString("sender_type"));
        } catch (JSONException e) {
        }
        try {
            this.sender = new Node(json.getJSONObject("sender"));
        } catch (JSONException e) {
        }
        try {
            this.data = new Data(json.getJSONObject("data"));
        } catch (JSONException e) {
        }
        try {
            this.store = new HashMap<String, Data<?>>();
            JSONObject obj = json.getJSONObject("store");
            Set<String> set = obj.keySet();
            for (String s : set) {
                store.put(s, new Data(obj.getJSONObject(s)));
            }
        } catch (JSONException | ClassCastException e) {
        }
    }

    public Message(MSG_TYPE type, SENDER_TYPE sender_type, Node sender, Data<?> data) {
        this.type = type;
        this.sender_type = sender_type;
        this.sender = sender;
        this.data = data;
    }

//    public Message(MSG_TYPE type, SENDER_TYPE sender, Data<?> data) {
//        this.type = type;
//        this.sender_type = sender;
//        this.data = data;
//    }

    public Message(Node sender, Data<?> data) {
        this.sender = sender;
        this.data = data;
    }

    public Message(Node sender, Map<String, Data<?>> store) {
        this.sender = sender;
        this.store = store;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        if (type != null) json.put("type", type.name());
        if (sender_type != null) json.put("sender_type", sender_type.name());
        if (sender != null)
            json.put("sender", sender.toJson());
        if (data != null)
            json.put("data", data.toJson());
        json.put("store", store);
        return json;
    }

    public MSG_TYPE getType() {
        return type;
    }

    public void setType(MSG_TYPE type) {
        this.type = type;
    }

    public SENDER_TYPE getSender_type() {
        return sender_type;
    }

    public void setSender_type(SENDER_TYPE sender_type) {
        this.sender_type = sender_type;
    }

    public Data<?> getData() {
        return data;
    }

    public void setData(Data<?> data) {
        this.data = data;
    }

    public Node getSender() {
        return sender;
    }

    public void setSender(Node sender) {
        this.sender = sender;
    }

    public Map<String, Data<?>> getStore() {
        return store;
    }

    public void setStore(Map<String, Data<?>> store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", sender_type=" + sender_type +
                ", sender=" + sender +
                ", data=" + data +
                ", store=" + store +
                '}';
    }
}
