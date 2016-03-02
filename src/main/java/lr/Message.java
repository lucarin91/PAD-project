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
    private String sender_ip;
    private int sender_port;
    private Data<?> data;
    private Map<String, Data<?>> store;

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", sender_type=" + sender_type +
                ", sender_ip='" + sender_ip + '\'' +
                ", sender_port=" + sender_port +
                ", data=" + data +
                ", store=" + store +
                '}';
    }

    public Map<String, Data<?>> getStore() {
        return store;
    }

    public void setStore(Map<String, Data<?>> store) {
        this.store = store;
    }

    public Message(JSONObject json) {
        try {
            this.type = MSG_TYPE.valueOf(json.getString("type"));
            this.sender_type = SENDER_TYPE.valueOf(json.getString("sender_type"));
        } catch (JSONException e) {
        }
        try {
            this.sender_ip = json.getString("sender_ip");
            this.sender_port = json.getInt("sender_port");
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
            for (String s : set){
                store.put(s, new Data(obj.getJSONObject(s)));
            }
        } catch (JSONException | ClassCastException e) {
        }
    }

    public Message(MSG_TYPE type, SENDER_TYPE sender, String sender_ip, int sender_port, Data<?> data) {
        this.type = type;
        this.sender_type = sender;
        this.sender_ip = sender_ip;
        this.sender_port = sender_port;
        this.data = data;
    }

    public Message(MSG_TYPE type, SENDER_TYPE sender, Data<?> data) {
        this.type = type;
        this.sender_type = sender;
        this.data = data;
    }

    public Message(Data<?> data) {
        this.data = data;
    }

    public Message(Map<String,Data<?>> store) {
        this.store = store;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        if (type != null) json.put("type", type.name());
        if (sender_type != null) json.put("sender_type", sender_type.name());
        json.put("sender_ip", sender_ip);
        json.put("sender_port", sender_port);
        if (data != null) {
            json.put("data", data.toJson());
        }
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

    public String getSender_ip() {
        return sender_ip;
    }

    public void setSender_ip(String sender_ip) {
        this.sender_ip = sender_ip;
    }

    public int getSender_port() {
        return sender_port;
    }

    public void setSender_port(int sender_port) {
        this.sender_port = sender_port;
    }

}
