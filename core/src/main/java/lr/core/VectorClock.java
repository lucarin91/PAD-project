package lr.core;


import java.io.Serializable;
import java.util.*;

/**
 * Created by luca on 06/03/16.
 */


public class VectorClock implements Serializable {

    //public static final int DEFAULT_N = 10;

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
    //    public COMP_CLOCK compareTo(VectorClock that) {
//        if (this == that) return COMP_CLOCK.EQUAL;
//        if (this.vector.length != that.vector.length) return COMP_CLOCK.NOTHING;
//
//        List<COMP_CLOCK> res = new ArrayList<>();
//        for (int i = 0; i < vector.length; i++) {
//            if (vector[i] > that.vector[i])
//                res.add(COMP_CLOCK.AFTER);
//            else if (vector[i] < that.vector[i])
//                res.add(COMP_CLOCK.BEFORE);
//            else
//                res.add(COMP_CLOCK.EQUAL);
//        }
//        return res.stream().allMatch(c -> c.equals(COMP_CLOCK.EQUAL)) ?
//                COMP_CLOCK.EQUAL :
//                res.stream().allMatch(c -> c.equals(COMP_CLOCK.BEFORE) || c.equals(COMP_CLOCK.EQUAL)) ?
//                        COMP_CLOCK.BEFORE :
//                        res.stream().allMatch(c -> c.equals(COMP_CLOCK.AFTER) || c.equals(COMP_CLOCK.EQUAL)) ?
//                                COMP_CLOCK.AFTER :
//                                COMP_CLOCK.NOTHING;
//    }

    public COMP_CLOCK compareTo(VectorClock that) {
        if (this == that) return COMP_CLOCK.EQUAL;

        Map<String, COMP_CLOCK> res = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.addAll(vector.keySet());
        set.addAll(that.vector.keySet());
        for (String key : set) {
            Long v1 = this.vector.getOrDefault(key, (long) 0);
            Long v2 = that.vector.getOrDefault(key, (long) 0);
            if (v1 > v2) {
                res.put(key, COMP_CLOCK.AFTER);
            } else if (v1 < v2) {
                res.put(key, COMP_CLOCK.BEFORE);
            } else
                res.put(key, COMP_CLOCK.EQUAL);
        }

        return res.values().stream().allMatch(c -> c.equals(COMP_CLOCK.EQUAL)) ?
                COMP_CLOCK.EQUAL :
                res.values().stream().allMatch(c -> c.equals(COMP_CLOCK.BEFORE) || c.equals(COMP_CLOCK.EQUAL)) ?
                        COMP_CLOCK.BEFORE :
                        res.values().stream().allMatch(c -> c.equals(COMP_CLOCK.AFTER) || c.equals(COMP_CLOCK.EQUAL)) ?
                                COMP_CLOCK.AFTER :
                                COMP_CLOCK.NOTHING;
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
