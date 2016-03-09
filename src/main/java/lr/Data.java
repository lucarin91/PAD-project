package lr;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lr.Messages.MessageManage;
import lr.Messages.MessageRequest;
import lr.Messages.MessageResponse;
import lr.Messages.MessageStatus;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by luca on 28/02/16.
 */

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class Data<T> implements Serializable {

    private String key;
    private Long hash;
    @JsonTypeInfo( use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY )
    private T value;
    private VectorClock version;

    public Data(String key, Long hash, T value) {
        this(key, hash, value, null);
    }

    public Data(String key, Long hash, T value, VectorClock clock) {
        this.key = key;
        this.hash = hash;
        this.value = value;
        this.version = clock;
    }

    public Data(String key) {
        this.key = key;
    }


    public Data(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public Data() {
    }

    public VectorClock getVersion() {
        return version;
    }

    public void setVersion(VectorClock version) {
        this.version = version;
    }

    //    public Data(JSONObject json) {
//        this.key = json.getString("key");
//        this.hash = json.getInt("hash");
//        try {
//
//            this.value = (T) json.get("value");
//        } catch (JSONException e) {
//        }
//    }
//
//    public JSONObject toJson() {
//        JSONObject obj = new JSONObject();
//        obj.add("key", key);
//        obj.add("hash", hash);
//        obj.add("value", value);
//        return obj;
//    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Data{" +
                "key='" + key + '\'' +
                ", hash=" + hash +
                ", value=" + value +
                ", version=" + version +
                '}';
    }

    public void setHash(Long hash) {
        this.hash = hash;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Long getHash() {
        return hash;
    }

    public T getValue() {
        return value;
    }

}
