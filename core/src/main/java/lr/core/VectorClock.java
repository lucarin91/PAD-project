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

    public Map<String, Long> getVector() {
        return vector;
    }

    public void setVector(Map<String, Long> vector) {
        this.vector = vector;
    }

    public COMP_CLOCK compareTo(VectorClock that) {
        if (this == that) return COMP_CLOCK.EQUAL;
        Map<String, COMP_CLOCK> res = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.addAll(vector.keySet());
        set.addAll(that.vector.keySet());
        for (String key : set) {
            Long v1 = this.vector.getOrDefault(key, (long) 0);
            Long v2 = that.vector.getOrDefault(key, (long) 0);
            res.put(key, v1 > v2 ? COMP_CLOCK.AFTER : v1 < v2 ? COMP_CLOCK.BEFORE : COMP_CLOCK.EQUAL);
        }

        return res.values().parallelStream().reduce((c1, c2) -> {
            if (c1 == c2) return c1;
            else if (c1 == COMP_CLOCK.EQUAL) return c2;
            else if (c2 == COMP_CLOCK.EQUAL) return c1;
            else return COMP_CLOCK.NOTHING;
        }).get();
    }

    public void update(VectorClock that) {
        for (Map.Entry<String, Long> item : that.vector.entrySet()) {
            vector.merge(item.getKey(), item.getValue(), (v1, v2) -> v2 > v1 ? v2 : v1);
        }
    }

    public VectorClock increment(String n) {
        vector.put(n, vector.getOrDefault(n, (long) 0) + 1);
        return this;
    }

    public long get(String n) {
        return vector.getOrDefault(n, (long) 0);
    }
}
