import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import lr.Data;
import lr.Messages.Message;
import lr.Messages.MessageManage;
import lr.Messages.MessageRequest;
import lr.Messages.MessageResponse;
import lr.Node;
import lr.NodeService;
import lr.front_end.GossipResource;
import org.junit.Assert;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by luca on 07/03/16.
 */
public class FSTest {

    final String key = "test";
    final String value = "lkjdsalkjdsakjldsalkjlkjdsalkjdsa";

    public void add(Node n, String value) {
        n.send(new MessageRequest<>(null, Message.MSG_OPERATION.ADD, key, value));
    }

    public void get(Node n, String value) {
        GossipResource g = GossipResource.getInstance("rest", "127.0.0.2", 2000, new ArrayList<>());
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.GET, key));
        g.<MessageResponse<Data<?>>>receive().ifPresent(message -> {
            Assert.assertEquals(message.getStatus(), MessageResponse.MSG_STATUS.OK);
            if (message.getStatus() == MessageResponse.MSG_STATUS.OK)

                Assert.assertEquals(value, ((Data<?>) message.getData()).getValue());
        });
    }

    public void rm(Node n) {
        n.send(new MessageRequest<>(null, Message.MSG_OPERATION.DEL, key));

    }


    public void up(Node n, String v) {
        n.send(new MessageRequest<>(null, Message.MSG_OPERATION.UP, key, v));

    }

    @Test
    public void testAll() {
        Node n = null;
        try {
            n = new NodeService("127.0.0.1", 2000, "TEST", new ArrayList<>());

            rm(n);

            add(n, value);

            Thread.sleep(1000);

            get(n, value);

            Thread.sleep(1000);

            rm(n);

            Thread.sleep(1000);
//
//            get(n,value);
//
//            Thread.sleep(1000);

            add(n, value);

            Thread.sleep(1000);

            up(n, value + 2);

            Thread.sleep(1000);

            get(n, value + 2);
        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
//            if (n != null) n.shutdown();
//            GossipResource.getInstance().ifPresent(GossipResource::shutdown);
        }
    }
}
