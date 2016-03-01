package lr;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.code.gossip.*;
import com.google.code.gossip.event.GossipState;
import com.google.code.gossip.manager.random.RandomGossipManager;
import ie.ucd.murmur.MurmurHash;
import org.apache.log4j.Logger;

import com.google.code.gossip.event.GossipListener;
import com.google.code.gossip.manager.GossipManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NodeService extends Node {

    public static final Logger LOGGER = Logger.getLogger(GossipService.class);

    private GossipManager _gossipManager;
    private ConsistentHash<Node> _ch;
    private Thread _passiveThread;
    private AtomicBoolean _toStop;
    private DatagramSocket _server;
//    private String _ip;
//    private int _portG;
//    private int _portM;
//    private String _id;
//    public NodeService(StartupSettings startupSettings) throws InterruptedException, UnknownHostException {
//        this(InetAddress.getLocalHost().getHostAddress(), startupSettings.getPort(), "",
//                startupSettings.getLogLevel(), startupSettings.getGossipMembers(), startupSettings
//                        .getGossipSettings(), this::callback);
//    }

    public NodeService(String ipAddress, int port, String id_, int logLevel, List<GossipMember> gossipMembers, GossipSettings settings)
            throws InterruptedException, UnknownHostException {
        super(id_, ipAddress, port);

        _ch = new ConsistentHash<>();
        for (GossipMember m : gossipMembers) {
            _ch.add(new Node(m));
        }
        _ch.add(this);

        _toStop = new AtomicBoolean(false);
//        _ip = ipAddress;
//        _portG = port;
//        _portM = port + 1;
//        _id = id;

        _gossipManager = new RandomGossipManager(ip, portG, id, settings, gossipMembers, this::callback);
        try {
            _server = new DatagramSocket(new InetSocketAddress(ip, portM));
        } catch (SocketException ex) {
            ex.printStackTrace();
            //throw new RuntimeException(ex);
        }

        _passiveThread = new Thread(this::passiveRequest);
        _passiveThread.start();
    }

    public void start() {
        _gossipManager.start();
    }

    /***
     * Type of message:
     * <p>
     * GET
     * {"type": "GET", "key": ..., "hash": ... }
     * <p>
     * ADD
     * {"type": "ADD", "key": ..., "hash": ..., "value": ....}
     * UP
     * {"type": "UP", "key": ..., "hash": ..., "value": ....}
     * <p>
     * DEL
     * {"type": "DEL", "key": ...., "hash": ... }
     */
    private void passiveRequest() {
        while (!_toStop.get()) {
            try {
                byte[] buf = new byte[_server.getReceiveBufferSize()];
                DatagramPacket p = new DatagramPacket(buf, buf.length);
                _server.receive(p);

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
                    String type = json.getString("type");

                    Data data = new Data<String>(json);
                    System.out.print(id+".RECEIVE: ");
                    switch (type) {
                        case "GET":
                            System.out.println("GET");
                            // TODO: send back th data to the requestor
                            break;

                        case "ADD":
                            System.out.println("add data "+data);
                            // TODO: save the new data
                            break;

                        case "DEL":
                            System.out.println("DEL");
                            // TODO: remove the data
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
                _toStop.set(true);
            }
        }
        shutdown();
    }

    public boolean addData(Data<?> data) {
        System.out.println(id+".SEND: add data "+data);
        try {
            Node n = _ch.get(data.getHash());
            if (n != null) {
                InetAddress dest = InetAddress.getByName(n.getIp());
                JSONObject json = new JSONObject();
                json.put("type", "ADD");
                json.put("key", data.getKey());
                json.put("hash", data.getHash());
                json.put("value", data.getValue());

                byte[] json_bytes = json.toString().getBytes();
                int packet_length = json_bytes.length;
                //TODO check packet size

                // Convert the packet length to the byte representation of the int.
                byte[] length_bytes = new byte[4];
                length_bytes[0] = (byte) (packet_length >> 24);
                length_bytes[1] = (byte) ((packet_length << 8) >> 24);
                length_bytes[2] = (byte) ((packet_length << 16) >> 24);
                length_bytes[3] = (byte) ((packet_length << 24) >> 24);

                ByteBuffer byteBuffer = ByteBuffer.allocate(4 + json_bytes.length);
                byteBuffer.put(length_bytes);
                byteBuffer.put(json_bytes);
                byte[] buf = byteBuffer.array();

                DatagramSocket socket = new DatagramSocket();
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, dest, n.getPortM());
                socket.send(datagramPacket);
                socket.close();
                return true;
            }
        } catch (JSONException | IOException e2) {
            e2.printStackTrace();
        }
        return false;
    }

    public void shutdown() {
        _gossipManager.shutdown();
        _server.close();
        try {
            _passiveThread.wait();
            System.out.println("after join");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public GossipManager get_gossipManager() {
        return _gossipManager;
    }

    public void set_gossipManager(GossipManager _gossipManager) {
        this._gossipManager = _gossipManager;
    }

    private void callback(GossipMember member, GossipState state) {
        if (state.equals(GossipState.UP))
            _ch.add(new Node(member));
        else
            _ch.remove(new Node(member));
        System.out.println(id + ". " + member + " " + state);
    }

    public void printStatus() {
        System.out.println("ID: " + id + " IP: " + ip + " list: " + _gossipManager.getMemberList().size() + " ch: " + _ch.size() + "\n" +
                "CH: " + _ch + "\n");
    }
}
