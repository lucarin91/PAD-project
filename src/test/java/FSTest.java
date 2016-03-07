import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import lr.Data;
import lr.Messages.Message;
import lr.Messages.MessageManage;
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
        n.send(new MessageManage(Message.MSG_TYPE.REQUEST, Message.MSG_OPERATION.ADD, null, Optional.of(new Data<>(key, value))));
    }

    public void get(Node n, String value) {
        GossipResource g = GossipResource.getInstance("rest", "127.0.0.2", 2000, new ArrayList<>());
        n.send(new MessageManage(Message.MSG_TYPE.REQUEST, Message.MSG_OPERATION.GET, g, Optional.of(new Data<>(key))));
        g.<MessageManage>receive().ifPresent(message -> {
            message.getData().ifPresent(data -> {
                Assert.assertEquals(value, data.getValue());
            });
        });
    }

    public void rm(Node n) {
        n.send(new MessageManage(Message.MSG_TYPE.REQUEST, Message.MSG_OPERATION.DEL, null, Optional.of(new Data<>(key))));

    }


    public void up(Node n, String v) {
        n.send(new MessageManage(Message.MSG_TYPE.REQUEST, Message.MSG_OPERATION.UP, null, Optional.of(new Data<>(key, v))));

    }

    @Test
    public void testAll() {
        try {
            NodeService n = new NodeService("127.0.0.1", 2000, "1", new ArrayList<>());

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
        }
    }
}
