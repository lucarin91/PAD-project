package lr.Messages;

/**
 * Created by luca on 02/03/16.
 */

import lr.Data;
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

public class MessageManage extends Message{
    private Data<?> data;

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

    public MessageManage(){ }
    public MessageManage(MSG_TYPE type, MSG_OPERATION oper, Node sender, Data<?> data) {
        super(type, oper, sender);
        this.data = data;
    }

    public MessageManage(MSG_TYPE type, Node sender, Data<?> data) {
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
//        if (type != null) json.put("type", type.name());
//        if (sender_type != null) json.put("sender_type", sender_type.name());
//        if (sender != null)
//            json.put("sender", sender.toJson());
//        if (data != null)
//            json.put("data", data.toJson());
//        json.put("store", store);
//        return json;
//    }

    public Data<?> getData() {
        return data;
    }

    public void setData(Data<?> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageManage{" +
                "data=" + data +
                "} " + super.toString();
    }
}
