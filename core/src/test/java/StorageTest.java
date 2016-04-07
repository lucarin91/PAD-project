import lr.core.Data;
import lr.core.Helper;
import lr.core.PersistentStorage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Created by luca on 09/03/16.
 */
public class StorageTest {

    @Test
    public void operations(){
        PersistentStorage s = new PersistentStorage("testSorage", true);
        Data <?> data1 = new Data<>("test", Helper.MD5ToLong("test"), "prova");

        //Test add
        s.add(data1);
        Optional<Data<?>> data2 = s.get("test");
        Assert.assertEquals(data2.isPresent(), true);
        Assert.assertEquals(data2.get(), data1);
        Assert.assertEquals(data2.get().getValue(), data1.getValue());

        //Test Update
        data1 = new Data<>("test", Helper.MD5ToLong("test"), "prova updated!");
        s.update(data1);
        data2 = s.get("test");
        Assert.assertEquals(data2.isPresent(), true);
        Assert.assertEquals(data2.get(), data1);
        Assert.assertEquals(data2.get().getValue(), data1.getValue());

        //Test remove
        s.remove("test");
        data2 = s.get("test");
        Assert.assertEquals(data2.isPresent(), false);
    }
}
