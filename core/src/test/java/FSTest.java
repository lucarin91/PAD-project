import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;
//import lr.gossip.GossipMember;
//import lr.gossip.RemoteGossipMember;

import lr.core.*;
import lr.core.Exception.SendException;
import lr.core.Messages.Message;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageResponse;
import lr.core.Nodes.GossipResource;
import lr.core.Nodes.Node;
import lr.core.Nodes.StorageNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by luca on 07/03/16.
 */
public class FSTest {

    Map<String, Node> nodes;

    @Before
    public void initMultipleServer() throws UnknownHostException, InterruptedException {
        nodes = new HashMap<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        final int seedNodes = 3;
        int clusterMembers = 10;

        for (int i = 1; i < seedNodes + 1; ++i) {
            startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
        }

        for (int i = 1; i < clusterMembers + 1; ++i) {
            nodes.put(i + "", new StorageNode(i + "", "127.0.0." + i, 2000, startupMembers).clearStorage().start());
        }

        GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, startupMembers);

        Thread.sleep(10000);
    }

    @Test
    public void multyServer() throws InterruptedException {
        GossipResource r = GossipResource.getInstance().get();

        MessageResponse<?> m = rm("test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.ERROR);

//        Thread.sleep(1000);

        m = add("test", "prova");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

//        Thread.sleep(1000);

        m = get("test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);
        Assert.assertEquals(((Data<?>) m.getData()).getValue(), "prova");

//        Thread.sleep(1000);

        m = rm("test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

//        Thread.sleep(1000);

        m = get("test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.ERROR);

//        Thread.sleep(1000);

        m = add("test", "value");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

//        Thread.sleep(1000);

        m = up("test", "value2");
        Assert.assertEquals(MessageResponse.MSG_STATUS.OK, m.getStatus());

//        Thread.sleep(1000);

        m = get("test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);
        Assert.assertEquals(((Data<?>) m.getData()).getValue(), "value2");
    }

    @After
    public void close() {
        if (nodes != null) nodes.values().forEach(Node::shutdown);
        GossipResource.getInstance().ifPresent(GossipResource::shutdown);
    }


    private MessageResponse<?> sendRequest(MessageRequest<?> msg) {
        try {
            return GossipResource.sendRequestToRandomNode(msg);
        } catch (SendException sendException) {
            throw new AssertionError();
        }
    }

    private MessageResponse<?> add(String key, String value) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.ADD, key, value));
    }

    private MessageResponse<?> get(String key) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.GET, key));
    }

    private MessageResponse<?> rm(String key) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.DELETE, key));
    }

    private MessageResponse<?> up(String key, String value) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.UPDATE, key, value));
    }
}
