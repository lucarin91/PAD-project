package lr.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;

/**
 * Created by luca on 28/02/16.
 */
public class Data<T> implements Serializable {

    private String key;
    private Long hash;
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
