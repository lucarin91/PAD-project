package lr;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by luca on 06/03/16.
 */


public class VectorClock implements Serializable{

    public static final int DEFAULT_N = 10;

    public enum COMP_CLOCK {
        BEFORE, AFTER, EQUAL, NOTHING
    }

    private long[] vector;

    public VectorClock() {
        this(DEFAULT_N);
    }

    public VectorClock(int n) {
        this.vector = new long[n];
        for (int i=0; i<n;i++) vector[i]=0;
    }

    public VectorClock(long[] n) {
        this.vector = n;
    }

    public long[] getVector() {
        return vector;
    }

    public void setVector(long[] vector) {
        this.vector = vector;
    }

    public COMP_CLOCK compareTo(VectorClock that) {
        if (this == that) return COMP_CLOCK.EQUAL;
        if (this.vector.length != that.vector.length) return COMP_CLOCK.NOTHING;

        List<COMP_CLOCK> res = new ArrayList<>();
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] > that.vector[i])
                res.add(COMP_CLOCK.AFTER);
            else if (vector[i] < that.vector[i])
                res.add(COMP_CLOCK.BEFORE);
            else
                res.add(COMP_CLOCK.EQUAL);
        }
        return res.stream().allMatch(c -> c.equals(COMP_CLOCK.EQUAL)) ?
                COMP_CLOCK.EQUAL :
                res.stream().allMatch(c -> c.equals(COMP_CLOCK.BEFORE) || c.equals(COMP_CLOCK.EQUAL)) ?
                        COMP_CLOCK.BEFORE :
                        res.stream().allMatch(c -> c.equals(COMP_CLOCK.AFTER) || c.equals(COMP_CLOCK.EQUAL)) ?
                                COMP_CLOCK.AFTER :
                                COMP_CLOCK.NOTHING;
    }

    public boolean update(VectorClock that) {
        if (vector.length != that.vector.length) return false;

        for (int i = 0; i < that.vector.length; i++)
            vector[i] = Math.max(vector[i], that.vector[i]);
        return true;
    }

    public long increment(int n){
        if (n >= vector.length) return -1;
        return ++vector[n];
    }

    public long get(int n){
        if (n >= vector.length) return -1;
        return vector[n];
    }
}
