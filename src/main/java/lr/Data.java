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

    public Data(String key, T value) {
        this.key = key;
        this.hash = MurmurHash.hash32(key);
        this.value = value;
    }

    public Data(JSONObject json) {
        try {
            this.key = json.getString("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.hash = json.getInt("hash");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //TODO: improve the cast to T
            this.value = (T) json.get("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setKey(String key) {
        this.key = key;
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

    @Override
    public String toString() {
        return "Data{" +
                "key='" + key + '\'' +
                ", hash=" + hash +
                ", value=" + value +
                '}';
    }
}
