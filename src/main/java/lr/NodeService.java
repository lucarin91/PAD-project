package lr;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.code.gossip.*;
import com.google.code.gossip.event.GossipState;
import com.google.code.gossip.manager.random.RandomGossipManager;
import org.apache.log4j.Logger;

import com.google.code.gossip.manager.GossipManager;
import org.json.JSONException;
import org.json.JSONObject;

public class NodeService extends Node {

    public static final Logger LOGGER = Logger.getLogger(GossipService.class);

    private GossipManager _gossipManager;
    private ConsistentHash<Node> _ch;
    private Thread _passiveThread;
    private AtomicBoolean _toStop;
    private DatagramSocket _server;
    private final int replica = 2;

    //TODO convert to persistent
    Map<String, Data<?>> _store;
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

        _store = new HashMap<>();

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
        _gossipManager.start();
    }
    /*
    public void start() {

    }*/

    /***
     * Type of message:
     * <p>
     * GET
     * {"type": "GET", "sender": FRONT|BACK, "key": ..., "hash": ... }
     * <p>
     * ADD
     * {"type": "ADD", "sender": FRONT|BACK, "key": ..., "hash": ..., "value": ....}
     * UP
     * {"type": "UP", "sender": FRONT|BACK, "key": ..., "hash": ..., "value": ....}
     * <p>
     * DEL
     * {"type": "DEL", "sender": FRONT|BACK, "key": ...., "hash": ... }
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
                    Message msg = new Message(json);

                    System.out.print(id + ".RECEIVE: ");
                    if (msg.getSender_type().equals(Message.SENDER_TYPE.FRONT)) {
                        System.out.println("from FRONT " + msg);
                        msg.setSender_type(Message.SENDER_TYPE.BACK);
                        if (msg.getType().equals(Message.MSG_TYPE.STATUS)){
                            send(msg.getSender_ip(), msg.getSender_port(), new Message(_store));
                        }else
                            _ch.get(msg.getData().getHash()).send(msg);
                    } else {
                        System.out.println("from BACK " + msg);
                        Data data = msg.getData();
                        switch (msg.getType()) {
                            case GET:
                                // TODO: send back th data to the requestor
                                send(msg.getSender_ip(), msg.getSender_port(), new Message(_store.get(data.getKey())));
                                break;

                            case ADD:
                                // TODO: save the new data
                                _store.putIfAbsent(data.getKey(), data);
                                ifMasterSendeUpdate(msg);
                                break;

                            case DEL:
                                // TODO: remove the data
                                _store.remove(data.getKey());
                                ifMasterSendeUpdate(msg);
                                break;

                            case UP:
                                // TODO: update the data
                                _store.replace(data.getKey(), data);
                                ifMasterSendeUpdate(msg);
                                break;
                            /*case STATUS:
                                send(msg.getSender_ip(), msg.getSender_port(), new Message(_store));
                                break;*/
                        }
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

    private void ifMasterSendeUpdate(Message msg) {
        if (msg.getSender_type().equals(Message.SENDER_TYPE.BACK)) {
            List<Node> list = _ch.getNext(toString(), replica);
            msg.setSender_type(Message.SENDER_TYPE.MASTER);
            for (Node n : list) {
                n.send(msg);
            }
        }
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
        if (!member.getId().contains("rest"))
            if (state.equals(GossipState.UP))
                _ch.add(new Node(member));
            else
                _ch.remove(new Node(member));
        //System.out.println(id + ". " + member + " " + state);
    }

    public void printStatus() {
        System.out.println("ID: " + id + " IP: " + ip + " list: " + _gossipManager.getMemberList().size() + " ch: " + _ch.size() + "\n" +
                "CH: " + _ch + "\n");
    }
}
