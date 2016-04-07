import lr.core.Data;
import lr.core.Helper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by luca on 07/04/16.
 */
public class DataTest {

    @Test
    public void base() {
        Data<String> d1 = new Data<>("test1", Helper.MD5ToLong("test1"), "value1");
        Data<String> d1Copy = new Data<>("test1", Helper.MD5ToLong("test1"), "value1");
        Data<String> d2 = new Data<>("test1", Helper.MD5ToLong("test1"), "value2");
        Data<String> d3 = new Data<>("test1", Helper.MD5ToLong("test1"), "value3");
        Data<String> d3Copy = new Data<>("test1", Helper.MD5ToLong("test1"), "value3");
        Data<String> d4 = new Data<>("test1", Helper.MD5ToLong("test1") ,"value4");
        Assert.assertFalse(d1.isConflict());
        Assert.assertFalse(d2.isConflict());
        Assert.assertFalse(d3.isConflict());
        Assert.assertFalse(d4.isConflict());


        Assert.assertTrue(d1Copy.addConflict(d2));
        Assert.assertTrue(d1Copy.isConflict());
        Assert.assertTrue(d1Copy.getConflictData().contains(d1));
        Assert.assertTrue(d1Copy.getConflictData().contains(d2));



        Assert.assertTrue(d3Copy.addConflict(d1Copy));
        Assert.assertTrue(d3Copy.isConflict());
        Assert.assertTrue(d3Copy.getConflictData().contains(d3));
        Assert.assertTrue(d3Copy.getConflictData().contains(d1));
        Assert.assertTrue(d3Copy.getConflictData().contains(d2));



        Data<String> dataConflict = new Data<>(d1, d2, d3, d4);
        Assert.assertTrue(dataConflict.isConflict());
        Assert.assertTrue(dataConflict.getConflictData().contains(d3));
        Assert.assertTrue(dataConflict.getConflictData().contains(d1));
        Assert.assertTrue(dataConflict.getConflictData().contains(d2));
        Assert.assertTrue(dataConflict.getConflictData().contains(d4));
    }
}
