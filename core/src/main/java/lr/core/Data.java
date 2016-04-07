package lr.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by luca on 28/02/16.
 */
public class Data<T> implements Serializable {

    private String key;
    private Long hash;
    private T value;
    private Set<Data<?>> conflictData;
    private VectorClock version;

    public Data() {
        this.conflictData = new HashSet<>();
        this.version = new VectorClock();
    }

    public Data(String key) {
        this();
        this.key = key;
    }

    public Data(String key, Long hash) {
        this(key);
        this.hash = hash;
    }

//    public Data(String key, T value) {
//        this(key);
//        this.value = value;
//    }

    public Data(String key, Long hash, T value) {
        this(key, hash);
        this.value = value;
    }

    public Data(String key, Long hash, T value, VectorClock clock) {
        this(key, hash, value);
        this.version = clock;
    }

    public Data(Data<?>... set) {
        this(set.clone()[0].getKey(), set.clone()[0].getHash());
        for (Data<?> d : set) {
            addConflict(d);
        }
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

    @JsonProperty("conflict")
    public boolean isConflict() {
        return conflictData.size() > 0;
    }

    @JsonIgnore
    public void setConflict(boolean ignored) {
    }

    public Set<Data<?>> getConflictData() {
        return conflictData;
    }

    public void setConflictData(Set<Data<?>> conflict) {
        this.conflictData = conflict;
    }

    public boolean addConflict(Data<?> data) {
        if (!data.getKey().equals(getKey())) return false;
        if (!isConflict()){
            conflictData.add(new Data<>(key,hash,value,version));
            version = null;
            value = null;
        }
        if (data.isConflict()) {
            conflictData.addAll(data.getConflictData());
        } else {
            conflictData.add(data);
        }

        return true;
    }

    @Override
    public String toString() {
        return "Data{" +
                "key='" + key + '\'' +
                ", hash=" + hash +
                ", value=" + value +
                ", conflictData=" + conflictData +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data<?> data = (Data<?>) o;

        if (key != null ? !key.equals(data.key) : data.key != null) return false;
        if (hash != null ? !hash.equals(data.hash) : data.hash != null) return false;
        if (value != null ? !value.equals(data.value) : data.value != null) return false;
        if (conflictData != null ? !conflictData.equals(data.conflictData) : data.conflictData != null) return false;
        return version != null ? version.equals(data.version) : data.version == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (conflictData != null ? conflictData.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
