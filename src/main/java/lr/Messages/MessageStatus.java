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

    //    public MessageManage(JSONObject json) {
//        try {
//            this.type = MSG_TYPE.valueOf(json.getString("type"));
//            this.sender_type = SENDER_TYPE.valueOf(json.getString("sender_type"));
//        } catch (JSONException e) {
//        }
//        try {
//            this.sender = new Node(json.getJSONObject("sender"));
//        } catch (JSONException e) {
//        }
//        try {
//            this.data = new Data(json.getJSONObject("data"));
//        } catch (JSONException e) {
//        }
//        try {
//            this.store = new HashMap<String, Data<?>>();
//            JSONObject obj = json.getJSONObject("store");
//            Set<String> set = obj.keySet();
//            for (String s : set) {
//                store.add(s, new Data(obj.getJSONObject(s)));
//            }
//        } catch (JSONException | ClassCastException e) {
//        }
//    }
    public MessageStatus() {
    }

    public MessageStatus(Node sender, Map<String, Data<?>> store, TreeMap<Long, Node> ch) {
        super(MSG_TYPE.STATUS, sender);
        this.store = store;
        this.ch = ch;
    }

    public MessageStatus(Node sender) {
        this(sender, null, null);
    }
//
//    public JSONObject toJson() {
//        JSONObject json = new JSONObject();
//        if (type != null) json.add("type", type.name());
//        if (sender_type != null) json.add("sender_type", sender_type.name());
//        if (sender != null)
//            json.add("sender", sender.toJson());
//        if (data != null)
//            json.add("data", data.toJson());
//        json.add("store", store);
//        return json;
//    }

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
