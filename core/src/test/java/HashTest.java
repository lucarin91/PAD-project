import lr.core.Helper;
import org.junit.Test;

/**
 * Created by luca on 07/03/16.
 */
public class HashTest {
    @Test
    public void md5(){
        System.out.println(Helper.MD5ToString("test"));
        System.out.println(Helper.MD5ToInt("test"));
        System.out.println(Helper.MD5ToLong("test"));
    }

}
