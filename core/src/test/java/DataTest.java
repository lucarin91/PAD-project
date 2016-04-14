import lr.core.Data;
import lr.core.Helper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by luca on 07/04/16.
 */
public class DataTest {
    private Data<String> d1;
    private Data<String> d1Conflict;
    private Data<String> d2;
    private Data<String> d3;
    private Data<String> d3Conflict;
    private Data<String> d4;

    @Before
    public void setup() {
        d1 = new Data<>("test1", Helper.MD5ToLong("test1"), "value1");
        d1Conflict = new Data<>("test1", Helper.MD5ToLong("test1"), "value1");

        d2 = new Data<>("test1", Helper.MD5ToLong("test1"), "value2");

        d3 = new Data<>("test1", Helper.MD5ToLong("test1"), "value3");
        d3Conflict = new Data<>("test1", Helper.MD5ToLong("test1"), "value3");

        d4 = new Data<>("test1", Helper.MD5ToLong("test1"), "value4");
    }

    @Test
    public void base() {
        Assert.assertFalse(d1.isConflict());
        Assert.assertFalse(d2.isConflict());
        Assert.assertFalse(d3.isConflict());
        Assert.assertFalse(d4.isConflict());
    }

    @Test
    public void conflict1() {
        Assert.assertTrue(d1Conflict.addConflict(d2));
        Assert.assertTrue(d1Conflict.isConflict());
        Assert.assertTrue(d1Conflict.getConflictData().contains(d1));
        Assert.assertTrue(d1Conflict.getConflictData().contains(d2));
    }

    @Test
    public void conflict2() {
        d1Conflict.addConflict(d2);

        Assert.assertTrue(d3Conflict.addConflict(d1Conflict));
        Assert.assertTrue(d3Conflict.isConflict());
        Assert.assertTrue(d3Conflict.getConflictData().contains(d3));
        Assert.assertTrue(d3Conflict.getConflictData().contains(d1));
        Assert.assertTrue(d3Conflict.getConflictData().contains(d2));

    }

    @Test
    public void conflict3() {
        d1Conflict.addConflict(d2);
        d3Conflict.addConflict(d1Conflict);

        Data<String> dataConflict = new Data<>(d1, d2, d3, d4);
        Assert.assertTrue(dataConflict.isConflict());
        Assert.assertTrue(dataConflict.getConflictData().contains(d3));
        Assert.assertTrue(dataConflict.getConflictData().contains(d1));
        Assert.assertTrue(dataConflict.getConflictData().contains(d2));
        Assert.assertTrue(dataConflict.getConflictData().contains(d4));
    }
}
