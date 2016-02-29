package lr;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.code.gossip.*;
import com.google.code.gossip.event.GossipState;
import com.google.code.gossip.manager.random.RandomGossipManager;
import org.apache.log4j.Logger;

import com.google.code.gossip.event.GossipListener;
import com.google.code.gossip.manager.GossipManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Node {

    public static final Logger LOGGER = Logger.getLogger(GossipService.class);

    private GossipManager _gossipManager;
    private ConsistentHash<RemoteNode> _ch;
    private Thread _passiveThread;
    private AtomicBoolean _toStop;
    private DatagramSocket _server;
    private String _ip;
    private int _portG;
    private int _portM;
    private String _id;
//    public Node(StartupSettings startupSettings) throws InterruptedException, UnknownHostException {
//        this(InetAddress.getLocalHost().getHostAddress(), startupSettings.getPort(), "",
//                startupSettings.getLogLevel(), startupSettings.getGossipMembers(), startupSettings
//                        .getGossipSettings(), this::callback);
//    }

    public Node(String ipAddress, int port, String id, int logLevel, List<GossipMember> gossipMembers, GossipSettings settings)
            throws InterruptedException, UnknownHostException {
        _ch = new ConsistentHash<>();
        for (GossipMember m : gossipMembers){
            _ch.add(new RemoteNode(m));
        }

        _toStop = new AtomicBoolean(false);
        _ip = ipAddress;
        _portG = port;
        _portM = port + 1;
        _id = id;

        _gossipManager = new RandomGossipManager(_ip, _portG, _id, settings, gossipMembers, this::callback);
        try {
            _server = new DatagramSocket(new InetSocketAddress(_ip, _portM));
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

                // TODO: check the data packat size

                byte[] json_bytes = new byte[packet_length];
                for (int i = 0; i < packet_length; i++) {
                    json_bytes[i] = buf[i + 4];
                }
                String receivedMessage = new String(json_bytes);
                try {

                    JSONObject json = new JSONObject(receivedMessage);
                    String type = json.getString("type");

                    Data data = new Data<String>(json);

                    switch (type) {
                        case "GET":
                            // TODO: send back th data to the requestor
                            break;

                        case "ADD":
                            // TODO: save the new data
                            break;

                        case "DEL":
                            // TODO: remove the data
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                // e.printStackTrace();
                _toStop.set(true);
            }
        }
        //shutdown();
    }


    public void shutdown() {
        _gossipManager.shutdown();
        _server.close();
        try {
            _passiveThread.join();
            System.out.println("after join");
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        _ch.add(new RemoteNode(member));
        System.out.println(_id + ". " + member + " " + state);
    }

    public void printStatus() {
        System.out.println("ID: " + _id + " IP: " + _ip + " list: " + _gossipManager.getMemberList().size() + " ch: " + _ch.size() + "\n" +
                "CH: " + _ch + "\n");
    }
}
