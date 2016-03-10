import lr.core.VectorClock.COMP_CLOCK;
import lr.core.VectorClock;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by luca on 06/03/16.
 */
public class TestVectorClock {



    @Test
    public void compare() {
        VectorClock c1 = new VectorClock();
        VectorClock c2 = new VectorClock();

        c1.increment("1");
        c1.increment("2");
        c2.increment("1");
        c2.increment("2");
        assertEquals(c1.compareTo(c2),COMP_CLOCK.EQUAL);

        c1.increment("1");
        assertEquals(c1.compareTo(c2), COMP_CLOCK.AFTER);

        c2.increment("2");
        assertEquals(c1.compareTo(c2), COMP_CLOCK.NOTHING);

        c1.increment("2");
        c2.increment("1");
        c2.increment("3");
        assertEquals(c1.compareTo(c2), COMP_CLOCK.BEFORE);
    }

    @Test
    public void update() {
        VectorClock c1 = new VectorClock();
        VectorClock c2 = new VectorClock();

        c1.increment("1");
        c1.increment("2");
        c2.update(c1);

        assertEquals(c1.compareTo(c2),COMP_CLOCK.EQUAL);

        c2.increment("3");
        c1.update(c2);
        assertEquals(c1.compareTo(c2),COMP_CLOCK.EQUAL);
    }
}
