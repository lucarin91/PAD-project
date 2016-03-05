package lr;

import ie.ucd.murmur.MurmurHash;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luca on 28/02/16.
 */
public class Data<T> {

    private String key;
    private int hash;
    private T value;

    public Data() {
    }

    public Data(String key) {
        this.key = key;
        this.hash = Helper.hash(key);
    }


    public Data(String key, T value) {
        this.key = key;
        this.hash = Helper.hash(key);
        this.value = value;
    }

    @Override
    public String toString() {
        return "Data{" +
                "key='" + key + '\'' +
                ", hash=" + hash +
                ", value=" + value +
                '}';
    }

//    public Data(JSONObject json) {
//        this.key = json.getString("key");
//        this.hash = json.getInt("hash");
//        try {
//            //TODO: improve the cast to T
//            this.value = (T) json.get("value");
//        } catch (JSONException e) {
//        }
//    }
//
//    public JSONObject toJson() {
//        JSONObject obj = new JSONObject();
//        obj.put("key", key);
//        obj.put("hash", hash);
//        obj.put("value", value);
//        return obj;
//    }

    public void setKey(String key) {
        this.key = key;
        this.hash = Helper.hash(key);
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getHash() {
        return hash;
    }

    public T getValue() {
        return value;
    }

}
