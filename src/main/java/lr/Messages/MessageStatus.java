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

public class MessageStatus extends Message{

    private Map<String, Data<?>> store;
    private TreeMap<Integer,Node> ch;

    public TreeMap<Integer,Node> getCh() {
        return ch;
    }

    public void setCh(TreeMap<Integer,Node> ch) {
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
//                store.put(s, new Data(obj.getJSONObject(s)));
//            }
//        } catch (JSONException | ClassCastException e) {
//        }
//    }
    public MessageStatus(){ }

    public MessageStatus(MSG_TYPE type, Node sender, Map<String, Data<?>> store, TreeMap<Integer,Node> ch) {
        super(type, MSG_OPERATION.STATUS, sender);
        this.store = store;
        this.ch = ch;
    }

    public MessageStatus(MSG_TYPE type, Node sender) {
        this(type, sender, null, null);
    }
//
//    public JSONObject toJson() {
//        JSONObject json = new JSONObject();
//        if (type != null) json.put("type", type.name());
//        if (sender_type != null) json.put("sender_type", sender_type.name());
//        if (sender != null)
//            json.put("sender", sender.toJson());
//        if (data != null)
//            json.put("data", data.toJson());
//        json.put("store", store);
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
