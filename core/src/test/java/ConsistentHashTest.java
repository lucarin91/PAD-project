import org.junit.Assert;
import lr.core.ConsistentHash;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by luca on 06/03/16.
 */
public class ConsistentHashTest {

    private final String TEST = "30";
    private final String TEST2 = "10000";
    private ConsistentHash<String> ch;

    @Before
    public void setup() {
        ch = new ConsistentHash<>(5, Integer::parseInt);

        for (int i = 1; i <= 10; i++) {
            ch.add(i + "");
        }
//        ch.getMap().entrySet().stream()
//                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }

    @Test
    public void base() {
        Assert.assertEquals("3", ch.get(TEST));
        Assert.assertEquals("1", ch.get(TEST2));
        Assert.assertEquals("2", ch.getPrev(TEST).getValue());
        Assert.assertEquals("3", ch.getNext(TEST).getValue());
        Assert.assertEquals("2", ch.getPrev(30).getValue());
        Assert.assertEquals("3", ch.getNext(30).getValue());

    }

    @Test
    public void getPrev() {

        ArrayList<Map.Entry<Long, String>> testPrev = ch.getPrev(TEST, 10);
        Assert.assertArrayEquals(new String[]{"2", "1", "10", "9", "8", "7", "6", "5", "4", "3"}, testPrev.stream().map(Map.Entry::getValue).toArray());

        testPrev = ch.getPrev(30, 10);
        Assert.assertArrayEquals(new String[]{"2", "1", "10", "9", "8", "7", "6", "5", "4", "3"}, testPrev.stream().map(Map.Entry::getValue).toArray());
    }

    @Test
    public void getNext() {
        ArrayList<Map.Entry<Long, String>> testNext = ch.getNext(TEST, 10);
        Assert.assertArrayEquals(new String[]{"3", "4", "5", "6", "7", "8", "9", "10", "1", "2"}, testNext.stream().map(Map.Entry::getValue).toArray());

        testNext = ch.getNext(30, 10);
        Assert.assertArrayEquals(new String[]{"3", "4", "5", "6", "7", "8", "9", "10", "1", "2"}, testNext.stream().map(Map.Entry::getValue).toArray());
    }
}
