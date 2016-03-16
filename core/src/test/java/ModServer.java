import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;
import lr.core.*;
import lr.core.Exception.SendRequestError;
import lr.core.Messages.Message;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageResponse;
import lr.core.Nodes.GossipResource;
import lr.core.Nodes.StorageNode;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by luca on 12/03/16.
 */
public class ModServer {
    @Test
    public void addServer() throws UnknownHostException, InterruptedException {
        final List<StorageNode> clients = new ArrayList<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        final int seedNodes = 2;
        int clusterMembers = 2;

        for (int i = 1; i < seedNodes + 1; ++i) {
            startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
        }

        for (int i = 1; i < clusterMembers + 1; ++i) {
            clients.add(new StorageNode(i + "", "127.0.0." + i, 2000, startupMembers)
                    .clearStorage()
                    .setNBackup(0)
                    .start());
        }

        GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, startupMembers);

        Thread.sleep(5000);

        System.out.print("key: " + Helper.MD5ToLong("hkjasdjkhdsahjkasdsad"));
        try {
            MessageResponse<?> msg = GossipResource.sendRequestToRandomNode(new MessageRequest<>(r, Message.MSG_OPERATION.ADD, "hkjasdjkhdsahjkasdsad", "PROVA"));
            Assert.assertEquals(MessageResponse.MSG_STATUS.OK, msg.getStatus());
        } catch (SendRequestError sendRequestError) {
            sendRequestError.printStackTrace();
        }

        try {
            Thread.sleep(2000);
            clients.add(new StorageNode("3", "127.0.0.3", 2000, startupMembers)
                    .clearStorage()
                    .setNBackup(0)
                    .start());

            Thread.sleep(5000);

            try {
                MessageResponse<?> msg = GossipResource.sendRequestToRandomNode(new MessageRequest<>(r, Message.MSG_OPERATION.DEL, "hkjasdjkhdsahjkasdsad"));
                Assert.assertEquals(MessageResponse.MSG_STATUS.OK, msg.getStatus());
            } catch (SendRequestError sendRequestError) {
                sendRequestError.printStackTrace();
            }

            StorageNode n = clients.get(2);
            try {
                Field storeField = StorageNode.class.getDeclaredField("_store");
                storeField.setAccessible(true);
                PersistentStorage store = (PersistentStorage) storeField.get(n);

                System.out.print("\t STORE of " + n.getId() + ": ");
                for (Map.Entry<String, Data<?>> i : store.getMap().entrySet()) {
                    System.out.print(i.getValue() + ", ");
                }
                Assert.assertEquals(0, store.getMap().size());
                System.out.println();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }


        } catch (UnknownHostException |
                InterruptedException e
                )

        {
            e.printStackTrace();
        }


//        while (true) {
//            System.out.print("\n...");
//            Thread.sleep(2000);
//            System.out.println();
//            for (StorageNode n : clients) {
//                try {
//                    Field privateField = StorageNode.class.getDeclaredField("_ch");
//                    privateField.setAccessible(true);
//                    ConsistentHash<Node> ch = (ConsistentHash<Node>) privateField.get(n);
//
//                    Map<Long, Node> map = ch.getMap();
//                    System.out.print("CH of " + n.getId() + "(" + ch.getHashesForKey(n.toString()) + "): ");
//                    for (Map.Entry<Long, Node> i : map.entrySet()) {
//                        System.out.print(i.getValue().getId() + ", ");
//                    }
//
//                    Field storeField = StorageNode.class.getDeclaredField("_store");
//                    storeField.setAccessible(true);
//                    PersistentStorage store = (PersistentStorage) storeField.get(n);
//
//                    System.out.print("\t STORE of " + n.getId() + ": ");
//                    for (Map.Entry<String, Data<?>> i : store.getMap().entrySet()) {
//                        System.out.print(i.getValue() + ", ");
//                    }
//                    System.out.println();
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }
}
