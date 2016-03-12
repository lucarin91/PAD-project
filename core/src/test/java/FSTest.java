import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;
import lr.core.Data;
import lr.core.GossipResource;
import lr.core.Messages.Message;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageResponse;
import lr.core.Node;
import lr.core.NodeService;
import org.junit.Assert;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by luca on 07/03/16.
 */
public class FSTest {

    public MessageResponse<?> add(Node n, String key, String value) {
        GossipResource g = GossipResource.getInstance().get();
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.ADD, key, value));
        return g.<MessageResponse>receive().get();
    }

    public MessageResponse<?> get(Node n, String key) {
        GossipResource g = GossipResource.getInstance().get();
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.GET, key));
        return g.<MessageResponse<?>>receive().get();
    }

    public MessageResponse<?> rm(Node n, String key) {
        GossipResource g = GossipResource.getInstance().get();
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.DEL, key));
        return g.<MessageResponse<?>>receive().get();
    }

    public MessageResponse<?> up(Node n, String key, String value) {
        GossipResource g = GossipResource.getInstance().get();
        n.send(new MessageRequest<>(g, Message.MSG_OPERATION.UP, key, value));
        return g.<MessageResponse<?>>receive().get();
    }

    @Test
    public void multiServerTest() {
        Collection<Node> servers = null;
        try {
            servers = initMultipleServer();
            test();
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (servers != null) servers.forEach(Node::shutdown);
            GossipResource.getInstance().ifPresent(GossipResource::shutdown);
        }
    }

    @Test
    public void singleServerTest() {
        Node n = null;
        try {
            List<GossipMember> members = new ArrayList<>();
            members.add(new RemoteGossipMember("127.0.0.1", 2000, "test-back"));
            n = new NodeService("test-back", "127.0.0.1", 2000, members).clearStorage().start();
            GossipResource.getInstance("test-rest", "127.0.0.20", 2000, members);
            Thread.sleep(2000);
            test();

        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (n != null) n.shutdown();
            GossipResource.getInstance().ifPresent(GossipResource::shutdown);
        }
    }

    public Collection<Node> initMultipleServer() throws UnknownHostException, InterruptedException {
        final Map<String, Node> clients = new HashMap<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        final int seedNodes = 3;
        int clusterMembers = 10;

        for (int i = 1; i < seedNodes + 1; ++i) {
            startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
        }

        for (int i = 1; i < clusterMembers + 1; ++i) {
            clients.put(i + "", new NodeService(i+"", "127.0.0." + i, 2000, startupMembers).clearStorage().start());
        }

        GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, startupMembers);

        Thread.sleep(10000);
        return clients.values();
    }

    public void test() throws InterruptedException {
        GossipResource r = GossipResource.getInstance().get();

        MessageResponse<?> m = rm(r.getRandomNode(), "test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.ERROR);

//        Thread.sleep(1000);

        m = add(r.getRandomNode(), "test", "prova");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

//        Thread.sleep(1000);

        m = get(r.getRandomNode(), "test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);
        Assert.assertEquals(((Data<?>) m.getData()).getValue(), "prova");

//        Thread.sleep(1000);

        m = rm(r.getRandomNode(), "test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

//        Thread.sleep(1000);

        m = get(r.getRandomNode(), "test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.ERROR);

//        Thread.sleep(1000);

        m = add(r.getRandomNode(), "test", "value");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);

//        Thread.sleep(1000);

        m = up(r.getRandomNode(), "test", "value2");
        Assert.assertEquals(MessageResponse.MSG_STATUS.OK, m.getStatus());

//        Thread.sleep(1000);

        m = get(r.getRandomNode(), "test");
        Assert.assertEquals(m.getStatus(), MessageResponse.MSG_STATUS.OK);
        Assert.assertEquals(((Data<?>) m.getData()).getValue(), "value2");
    }
}
