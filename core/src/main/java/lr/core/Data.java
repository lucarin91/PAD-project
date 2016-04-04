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

    public Data(String key, Long hash, T value) {
        this(key, hash, value, null);
    }

    public Data(String key, Long hash, T value, VectorClock clock) {
        this(key, value);
        this.hash = hash;
        this.version = clock;
    }

    public Data(String key) {
        this();
        this.key = key;
    }


    public Data(String key, T value) {
        this(key);
        this.value = value;
    }

    public Data(Set<Data<?>> set){
        this(set.iterator().next().getKey());
        setConflictData(set);
    }

    public Data() {
        this.conflictData = new HashSet<>();
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
    public boolean isConflict(){
        return conflictData.size()>0;
    }

    @JsonIgnore
    public void setConflict(boolean ignored) {
    }

    public Set<Data<?>> getConflictData() {
        return conflictData;
    }

    public void setConflictData(Set<Data<?>> conflictData) {
        conflictData.stream().filter(Data::isConflict).forEach(d1 -> {
            conflictData.addAll(d1.getConflictData());
            d1.setConflictData(new HashSet<>());
        });
        this.conflictData = conflictData;
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
