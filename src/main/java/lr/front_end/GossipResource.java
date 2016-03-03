package lr.front_end;

import com.google.code.gossip.GossipMember;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.LocalGossipMember;
import com.google.code.gossip.manager.GossipManager;
import com.google.code.gossip.manager.random.RandomGossipManager;
import lr.MessageManage;
import lr.Node;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luca on 01/03/16.
 */
public class GossipResource extends Node {

    private static GossipResource _r;
    private GossipManager _gossipManager;
    private DatagramSocket _server;
    private Random _random;

    public Node getRandomNode(){
        List<LocalGossipMember> list = _gossipManager.getMemberList();
        return new Node(list.get(_random.nextInt(list.size())));
    }

    public List<Node> getNode(){
        List<LocalGossipMember> list = _gossipManager.getMemberList();
        List<Node> res = list.stream().map(Node::new).collect(Collectors.toList());
        return res;
    }

    private GossipResource(String id, String ip, int port, List<GossipMember> gossipMembers) {
        super(id, ip, port);

        try {
            _server = new DatagramSocket(new InetSocketAddress(ip, portM));
        } catch (SocketException ex) {
            ex.printStackTrace();
            //throw new RuntimeException(ex);
        }


        _gossipManager = new RandomGossipManager(ip, port, id,  new GossipSettings(), gossipMembers, null);
        _gossipManager.start();
        _random = new Random();
    }

    public static GossipResource getInstance(String id, String ip, int port, List<GossipMember> gossipMembers) {
        if (_r == null) _r = new GossipResource(id,ip,port,gossipMembers);
        return _r;
    }

    public static Optional<GossipResource> getInstance(){
        return Optional.of(_r);
    }

    public Optional<MessageManage> receive(){
        //System.out.println("FRONT receive message...");
        try {
            byte[] buf = new byte[_server.getReceiveBufferSize()];

            DatagramPacket p = new DatagramPacket(buf, buf.length);
            _server.receive(p);

            System.out.println("FRONT get message..");
            int packet_length = 0;
            for (int i = 0; i < 4; i++) {
                int shift = (4 - 1 - i) * 8;
                packet_length += (buf[i] & 0x000000FF) << shift;
            }

            // TODO: check the data packet size

            byte[] json_bytes = new byte[packet_length];
            for (int i = 0; i < packet_length; i++) {
                json_bytes[i] = buf[i + 4];
            }
            String receivedMessage = new String(json_bytes);
            try {
                JSONObject json = new JSONObject(receivedMessage);
                return Optional.of(new MessageManage(json));
            } catch (JSONException e) {

            }
        }catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void shutdown() {
        _gossipManager.shutdown();
    }
}
