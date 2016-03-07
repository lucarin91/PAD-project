package lr.Messages;

/**
 * Created by luca on 02/03/16.
 */

import lr.Data;
import lr.Node;

import java.util.Optional;

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

public class MessageManage extends Message{
    private Optional<Data<?>> data;

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

    public MessageManage(){ }

    public MessageManage(MSG_TYPE type, MSG_OPERATION oper, Node sender, Optional<Data<?>> data) {
        super(type, oper, sender);
        this.data = data;
    }

    public MessageManage(MSG_TYPE type, Node sender, Optional<Data<?>> data) {
        this(type,null,sender,data);
    }

//    public MessageManage(MSG_TYPE type, SENDER_TYPE sender, Data<?> data) {
//        this.type = type;
//        this.sender_type = sender;
//        this.data = data;
//    }

//    public MessageManage(Node sender, Data<?> data) {
//        super(null,sender);
//        this.data = data;
//    }


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

    public void setData(Optional<Data<?>> data) {
        this.data = data;
    }

    public Optional<Data<?>> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MessageManage{" +
                "data=" + data +
                "} " + super.toString();
    }
}
