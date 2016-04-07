package lr.core;

import java.io.Serializable;
import java.util.*;

/**
 * Created by luca on 06/03/16.
 */
public class VectorClock implements Serializable {

    public enum COMP_CLOCK {
        BEFORE, AFTER, EQUAL, NOTHING
    }

    private Map<String, Long> vector;

    public VectorClock() {
        vector = new HashMap<>();
    }

    public VectorClock(String... ids){
        for (String id : ids){
            increment(id);
        }
    }

    public Map<String, Long> getVector() {
        return vector;
    }

    public void setVector(Map<String, Long> vector) {
        this.vector = vector;
    }

    public COMP_CLOCK compareTo(VectorClock that) {
        if (this == that) return COMP_CLOCK.EQUAL;

        Set<String> set = new HashSet<>();
        set.addAll(vector.keySet());
        set.addAll(that.vector.keySet());

        return set.parallelStream().map(key -> {
            Long v1 = this.vector.getOrDefault(key, 0L);
            Long v2 = that.vector.getOrDefault(key, 0L);
            return v1 > v2 ? COMP_CLOCK.AFTER : v1 < v2 ? COMP_CLOCK.BEFORE : COMP_CLOCK.EQUAL;
        }).reduce((c1, c2) -> {
            return c1 == c2 ? c1 : c1 == COMP_CLOCK.EQUAL ? c2 : c2 == COMP_CLOCK.EQUAL ? c1 : COMP_CLOCK.NOTHING;
        }).get();
    }

    public void update(VectorClock that) {
        for (Map.Entry<String, Long> item : that.vector.entrySet()) {
            vector.merge(item.getKey(), item.getValue(), (v1, v2) -> v2 > v1 ? v2 : v1);
        }
    }

    public VectorClock increment(String n) {
        vector.put(n, vector.getOrDefault(n, 0L) + 1);
        return this;
    }

    public long get(String n) {
        return vector.getOrDefault(n, 0L);
    }

    @Override
    public String toString() {
        return "VectorClock{" +
                "vector=" + vector +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VectorClock that = (VectorClock) o;

        return vector != null ? vector.equals(that.vector) : that.vector == null;

    }

    @Override
    public int hashCode() {
        return vector != null ? vector.hashCode() : 0;
    }
}
