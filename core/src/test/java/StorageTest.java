import lr.core.Data;
import lr.core.Helper;
import lr.core.PersistentStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

/**
 * Created by luca on 09/03/16.
 */
public class StorageTest {

    PersistentStorage s;
    Data<String> data;
    Optional<Data<?>> dataGet;

    @Before
    public void setup() {
        s = new PersistentStorage("testSorage", true);
        data = new Data<>("test", Helper.MD5ToLong("test"), "prova");
    }

    @Test
    public void add() {
        s.add(data);
        dataGet = s.get("test");
        Assert.assertEquals(dataGet.isPresent(), true);
        Assert.assertEquals(dataGet.get(), data);
        Assert.assertEquals(dataGet.get().getValue(), data.getValue());

    }

    @Test
    public void update() {
        s.add(data);

        data = new Data<>("test", Helper.MD5ToLong("test"), "prova updated!");
        s.update(data);
        dataGet = s.get("test");
        Assert.assertEquals(dataGet.isPresent(), true);
        Assert.assertEquals(dataGet.get(), data);
        Assert.assertEquals(dataGet.get().getValue(), data.getValue());
    }

    @Test
    public void remove() {
        s.add(data);

        s.remove("test");
        dataGet = s.get("test");
        Assert.assertEquals(dataGet.isPresent(), false);
    }
}
