package lr;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.code.gossip.*;
import com.google.code.gossip.event.GossipState;
import com.google.code.gossip.manager.random.RandomGossipManager;
import lr.Messages.*;
import lr.Messages.Message.*;
import org.apache.log4j.Logger;

import com.google.code.gossip.manager.GossipManager;
import org.json.JSONException;

public class NodeService extends Node {

    public static final Logger LOGGER = Logger.getLogger(GossipService.class);

    private GossipManager _gossipManager;
    private ConsistentHash<Node> _ch;
    private final Thread _passiveThread;
    private AtomicBoolean _toStop;
    private DatagramSocket _server;
    private final int replica = 2;
    //private VectorClock _clock;

    PersistentStorage _store;

//    private String _ip;
//    private int _portG;
//    private int _portM;
//    private String _id;
//    public NodeService(StartupSettings startupSettings) throws InterruptedException, UnknownHostException {
//        this(InetAddress.getLocalHost().getHostAddress(), startupSettings.getPort(), "",
//                startupSettings.getLogLevel(), startupSettings.getGossipMembers(), startupSettings
//                        .getGossipSettings(), this::callback);
//    }

    public NodeService(String ipAddress, int port, String id_, List<GossipMember> gossipMembers)
            throws InterruptedException, UnknownHostException {
        super(id_, ipAddress, port);

        _ch = new ConsistentHash<>();
        gossipMembers.forEach(gossipMember -> _ch.add(new Node(gossipMember)));
//        for (GossipMember m : gossipMembers) {
//            _ch.add(new Node(m));
//        }

        _ch.add(this);
        _store = new PersistentStorage(this);

        //_clock = new VectorClock();
        _toStop = new AtomicBoolean(false);
//        _ip = ipAddress;
//        _portG = port;
//        _portM = port + 1;
//        _id = id;

        _gossipManager = new RandomGossipManager(ip, portG, id, new GossipSettings(), gossipMembers, this::callback);
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
                System.arraycopy(buf, 4, json_bytes, 0, packet_length);
                String receivedMessage = new String(json_bytes);

                //_clock.increment(getId());
                try {
//                    JSONObject json = new JSONObject(receivedMessage);
//                    MessageManage msg = new MessageManage(json);
                    ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());
                    Message msg = mapper.readValue(receivedMessage, Message.class);

                    System.out.print(id + ".RECEIVE: ");

                    if (msg.getType().equals(MSG_TYPE.REQUEST)) {
                        //System.out.println("" + msg);

                        MessageManage msgM = (MessageManage) msg;//mapper.readValue(receivedMessage,MessageManage.class);
                        //msg.setSender(this);
                        msgM.getData().ifPresent(data -> {
                            Node n = _ch.get(data.getKey());

                            if (n.getId().equals(getId())) {
                                doOperation(msgM);
                            } else {
                                System.out.println("pass request.." + msgM);
                                n.send(msg);
                            }
                        });

                    } else if (msg.getType().equals(MSG_TYPE.STATUS)) {
                        send(msg.getSender().getIp(), msg.getSender().getPortM(), new MessageStatus(this, _store.getMap(), _ch.getMap()));
                    } else {
                        System.out.println("receive management.." + msg);
                        MessageManage msgM = (MessageManage) msg; //mapper.readValue(receivedMessage,MessageManage.class);
                        doManageOperation(msgM);
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

    private void doManageOperation(MessageManage msg) {
        System.out.println("do Management Operation... " + msg);
        msg.getData().ifPresent(thatData -> {
            VectorClock thatClock = thatData.getVersion();
            if (null != thatClock) {
//                if (_clock.compareTo(vectorClock).equals(VectorClock.COMP_CLOCK.BEFORE)) {
//                    _clock.update(vectorClock);
                switch (msg.getOperation()) {
                    case ADD:
                        _store.add(thatData);
                        break;
                    case UP:
                        _store.get(thatData.getKey()).ifPresent(thisData -> {
                            VectorClock thisClock = thisData.getVersion();
                            if (thisClock.compareTo(thatClock).equals(VectorClock.COMP_CLOCK.BEFORE))
                                _store.update(thatData);
                        });
                        break;
                }
                //}
            }
        });
    }

    private void doOperation(MessageManage msg) {
        System.out.println("do Operation... " + msg);
        msg.getData().ifPresent(data -> {
            switch (msg.getOperation()) {
                case GET:
                    _store.get(data.getKey()).ifPresent(data1 -> {
                        msg.getSender().send(new MessageManage(MSG_TYPE.RESPONSE, this, Optional.of(data1)));
                    });
                    //TODO: else, send an error
                    break;

                case ADD:
                    data.setVersion(new VectorClock().increment(getId()));
                    data.setHash(_ch.doHash(data.getKey()));
                    _store.add(data);
                    break;

                case DEL:
                    _store.remove(data.getKey());
                    break;

                case UP:
                    _store.get(data.getKey()).ifPresent(thisData -> {
                        data.setVersion(thisData.getVersion().increment(getId()));
                        _store.update(data);
                    });
                    break;
            }

            if (!msg.getOperation().equals(MSG_OPERATION.GET)) {
                msg.setData(Optional.of(data));
                System.out.println("propagate.." + msg);
                propagateRequest(msg);
            }

//            if (!msg.getOperation().equals(MSG_OPERATION.GET)) {
//                msg.setData(Optional.of(data));
//                System.out.println("propagate.." + msg);
//                propagateRequest(msg);
//            }
        });
    }

    private void propagateRequest(MessageManage msg) {
        List<Node> list = _ch.getNext(toString(), replica);
        System.out.println("send propagate to.." + list);
        msg.setType(MSG_TYPE.MANAGEMENT);
        for (Node n : list) n.send(msg);
    }

    public void shutdown() {
        _gossipManager.shutdown();
        _server.close();
        _store.close();
        try {
            synchronized (_passiveThread) {
                _passiveThread.wait();
            }
            System.out.println("after join");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @JsonIgnore
    public GossipManager get_gossipManager() {
        return _gossipManager;
    }

    @JsonIgnore
    public void set_gossipManager(GossipManager _gossipManager) {
        this._gossipManager = _gossipManager;
    }

    private void callback(GossipMember member, GossipState state) {
        if (!member.getId().contains("rest")) {
            Node n = new Node(member);
            if (state.equals(GossipState.UP)) {
                String info = getId() + ". NEW MEMBER [" + n + "] up ...";
                _ch.add(n);
                List<Node> next = _ch.getNext(toString(), replica);
                if (next.stream().anyMatch(node -> node.equals(n))) {
                    System.out.println(info + "SEND backup data [" + next + "]");
                    _store.getMap().forEach((s, data) -> {
                        n.send(new MessageManage(MSG_TYPE.MANAGEMENT, MSG_OPERATION.ADD, this, Optional.of(data)));
                    });
                }
                Node prev = _ch.getPrev(toString());
                if (prev.equals(n)) {
                    System.out.println(info + "SEND his data [" + prev + "]");
                    List<Long> hashList = _ch.getHashesForKey(n.toString());

                    _store.getMap().entrySet().parallelStream().filter(dataEntry -> {
                        return hashList.stream().anyMatch(hash -> {
                            return _ch.doHash(dataEntry.getValue().getKey()) < hash;
                        });
                    }).forEach(dataEntry -> {
                        n.send(new MessageManage(MSG_TYPE.MANAGEMENT, MSG_OPERATION.ADD, this, Optional.of(dataEntry.getValue())));
                    });
                }
            } else
                _ch.remove(n);
        }
    }

    public void printStatus() {
        System.out.println("ID: " + id + " IP: " + ip + " list: " + _gossipManager.getMemberList().size() + " ch: " + _ch.size() + "\n" +
                "CH: " + _ch + "\n");
    }
}
