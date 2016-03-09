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

    final GossipResource g = GossipResource.getInstance("rest", "127.0.0.2", 2000, new ArrayList<>());


    public MessageResponse<?> add(Node n, String key, String value) {
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.ADD, key, value));
        return g.<MessageResponse>receive().get();
    }

    public MessageResponse<?> get(Node n, String key) {
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.GET, key));
        return g.<MessageResponse<?>>receive().get();
    }

    public MessageResponse<?> rm(Node n, String key) {
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.DEL, key));
        return g.<MessageResponse<?>>receive().get();
    }


    public MessageResponse<?> up(Node n, String key, String value) {
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.UP, key, value));
        return g.<MessageResponse<?>>receive().get();
    }

    @Test
    public void testAll() {
        Node n = null;
        try {
            n = new NodeService("127.0.0.1", 2000, "TEST", new ArrayList<>());

            MessageResponse<?> m = rm(n, "test");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

            Thread.sleep(1000);

            m = add(n, "test", "prova");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

            Thread.sleep(1000);

            m = get(n, "test");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);
            Assert.assertEquals(((Data<?>)m.getData()).getValue(), "prova");

            Thread.sleep(1000);

            m = rm(n, "test");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

            Thread.sleep(1000);

            m = get(n, "test");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.ERROR);

            Thread.sleep(1000);

            m = add(n, "test", "value");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

            Thread.sleep(1000);

            m = up(n, "test", "value2");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

            Thread.sleep(1000);

            m = get(n, "test");
            Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);
            Assert.assertEquals(((Data<?>)m.getData()).getValue(), "value2");

        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (n != null) n.shutdown();
            GossipResource.getInstance().ifPresent(GossipResource::shutdown);
        }
    }
}
