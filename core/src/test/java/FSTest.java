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
import org.junit.Assert;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by luca on 07/03/16.
 */
public class FSTest {

    public MessageResponse<?> sendRequest(MessageRequest<?> msg) {
        try {
            return GossipResource.sendRequestToRandomNode(msg);
        } catch (SendException sendException) {
            throw new AssertionError();
        }
    }

    public MessageResponse<?> add(String key, String value) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.ADD, key, value));
    }

    public MessageResponse<?> get(String key) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.GET, key));
    }

    public MessageResponse<?> rm(String key) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.DELETE, key));
    }

    public MessageResponse<?> up(String key, String value) {
        return sendRequest(new MessageRequest<>(Message.MSG_OPERATION.UPDATE, key, value));
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
            n = new StorageNode("test-back", "127.0.0.1", 2000, members).clearStorage().start();
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
            clients.put(i + "", new StorageNode(i + "", "127.0.0." + i, 2000, startupMembers).clearStorage().start());
        }

        GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, startupMembers);

        Thread.sleep(10000);
        return clients.values();
    }

    public void test() throws InterruptedException {
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
}
