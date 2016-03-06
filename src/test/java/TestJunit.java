/**
 * Created by luca on 06/03/16.
 */
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class TestJunit {
    @Test
    public void testAdd() {
        String str= "Junit is working fine";
        assertEquals("Junit is working fine",str);
    }
}
