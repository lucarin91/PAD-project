import lr.VectorClock.COMP_CLOCK;
import lr.VectorClock;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by luca on 06/03/16.
 */
public class TestVectorClock {

    VectorClock c = new VectorClock(new long[]{1, 2, 3});
    VectorClock c_smoll = new VectorClock(new long[]{0, 0, 0});
    VectorClock c_big = new VectorClock(new long[]{2, 4, 3});
    VectorClock c_diff = new VectorClock(new long[]{0, 3, 1});
    VectorClock c_eq = new VectorClock(new long[]{1, 2, 3});
    VectorClock c_wrong = new VectorClock(new long[]{1, 2, 3, 5});

    @Test
    public void compare() {
        assertEquals(c.compareTo(c_smoll), COMP_CLOCK.AFTER);
        assertEquals(c.compareTo(c_big), COMP_CLOCK.BEFORE);
        assertEquals(c.compareTo(c_diff), COMP_CLOCK.NOTHING);
        assertEquals(c.compareTo(c_eq), COMP_CLOCK.EQUAL);

        assertEquals(c.compareTo(c_wrong), COMP_CLOCK.NOTHING);
        assertEquals(c.compareTo(c), COMP_CLOCK.EQUAL);
    }

    @Test
    public void update() {
        c.update(c_smoll);
        assertArrayEquals(c.getVector(), new long[]{1, 2, 3});

        c.update(c_big);
        assertArrayEquals(c.getVector(), new long[]{2, 4, 3});

        c.update(c_diff);
        assertArrayEquals(c.getVector(), new long[]{2, 4, 3});

        assertEquals(c.update(c_wrong), false);
    }

    @Test
    public void increment() {
        c.increment(0);
        assertArrayEquals(c.getVector(), new long[]{2, 2, 3});

        c.increment(1);
        assertArrayEquals(c.getVector(), new long[]{2, 3, 3});

        c.increment(2);
        assertArrayEquals(c.getVector(), new long[]{2, 3, 4});

        assertEquals(c.increment(3), -1);
    }
}
