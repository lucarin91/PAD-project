package lr.core.Nodes;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BinaryOperator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.code.gossip.*;
import com.google.code.gossip.event.GossipState;
import com.google.code.gossip.manager.random.RandomGossipManager;
import lr.core.*;
import lr.core.Exception.SendException;
import lr.core.Messages.*;
import lr.core.Messages.Message.*;
import org.apache.log4j.Logger;

import com.google.code.gossip.manager.GossipManager;

public class StorageNode extends Node {

    public static final Logger LOGGER = Logger.getLogger(GossipService.class);

    private GossipManager _gossipManager;
    private ConsistentHash<Node> _ch;
    private PersistentStorage _store;
    private final Thread _passiveThread;
    private AtomicBoolean _toStop;
    private DatagramSocket _server;
    private int _replica = 2;

    public StorageNode clearStorage() {
        _store.close();
        _store = new PersistentStorage(getId(), true);
        return this;
    }

    @JsonIgnore
    public StorageNode setNBackup(int replica) {
        _replica = replica;
        return this;
    }

    public StorageNode(String id_, String ipAddress, int port, List<GossipMember> gossipMembers)
            throws InterruptedException, UnknownHostException {
        super(id_, ipAddress, port);

        _ch = new ConsistentHash<>();
        gossipMembers.stream().filter(member -> !member.getId().contains(GossipResource.FRONT_ID))
                .forEach(gossipMember -> _ch.add(new Node(gossipMember)));
        _ch.add(this);

        _store = new PersistentStorage(getId());

        _toStop = new AtomicBoolean(false);

        _gossipManager = new RandomGossipManager(ip, portG, id, new GossipSettings(), gossipMembers, this::callback);
        try {
            _server = new DatagramSocket(new InetSocketAddress(ip, portM));
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        _passiveThread = new Thread(this::passiveRequest);
    }


    public StorageNode start() {
        _passiveThread.start();
        _gossipManager.start();
        return this;
    }

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

                ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());
                Message msg = mapper.readValue(receivedMessage, Message.class);

                System.out.print(id + ".RECEIVE: " + msg);

                if (msg instanceof MessageRequest<?>) {

                    MessageRequest msgR = (MessageRequest) msg;
                    if (msgR.getOperation() != MSG_OPERATION.STATUS) {
                        //msg.setSender(this);
                        Node n = _ch.get(msgR.getKey());

                        if (n.getId().equals(getId())) {
                            doOperation(msgR);
                        } else {
                            System.out.println("pass request..");
                            n.send(msg);
                        }

                    } else {
                        msgR.getSender().send(new MessageStatus(this, _store.getMap(), _ch.getMap()));
                    }
                } else if (msg instanceof MessageManage) {
                    System.out.println("receive management..");
                    MessageManage msgM = (MessageManage) msg;
                    doManageOperation(msgM);
                }
            } catch (SocketException e) {
                //e.printStackTrace();
                _toStop.set(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SendException e) {
            }
        }
    }

    private void doManageOperation(MessageManage msg) {
        System.out.println("do Management Operation... ");
        switch (msg.getOperation()) {
            case ADD:
                _store.add(msg.getData());
                break;

            case UPDATE:
                _store.update(msg.getData());
                break;

            case DELETE:
                _store.remove(msg.getKey());
                break;

            case ADDorUPDATE:
                Data<?> thatData = msg.getData();
                VectorClock thatClock = thatData.getVersion();
                Optional<Data<?>> optData = _store.get(msg.getData().getKey());
                if (optData.isPresent()) {
                    Data<?> thisData = optData.get();
                    VectorClock thisClock = thisData.getVersion();
                    switch (thisClock.compareTo(thatClock)) {
                        case BEFORE:
                            _store.update(msg.getData());
                            break;
                        case NOTHING:
                            Set<Data<?>> set = new HashSet<>();
                            set.add(thisData);
                            set.add(thatData);
                            Data<?> conflictData = new Data<>(set);
                            _store.update(conflictData);
                            break;
                        case AFTER:
                        case EQUAL:
                            break;
                    }
                } else
                    _store.add(msg.getData());
                break;
        }
    }

    private void doOperation(MessageRequest<?> msg) {
        System.out.println("do Operation... ");
        Node sender = msg.getSender();
        try {
            switch (msg.getOperation()) {
                case GET:
                    Optional<Data<?>> data = _store.get(msg.getKey());
                    if (data.isPresent()) {
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.OK, data.get()));
                    } else {
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.ERROR, MessageResponse.KEY_NOT_FOUND));
                    }
                    break;

                case ADD:
                    Data data1 = new Data<>(msg.getKey(),
                            _ch.doHash(msg.getKey()),
                            msg.getValue(),
                            new VectorClock().increment(getId()));
                    if (_store.add(data1)) {
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.OK));
                        sendBackup(new MessageManage(this, MSG_OPERATION.ADD, data1));
                    } else
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.ERROR, MessageResponse.KEY_ALREADY));
                    break;

                case DELETE:
                    if (_store.remove(msg.getKey())) {
                        sendBackup(new MessageManage(this, MSG_OPERATION.DELETE, msg.getKey()));
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.OK));
                    } else {
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.ERROR, MessageResponse.KEY_NOT_FOUND));
                    }
                    break;

                case UPDATE:
                    Optional<Data<?>> optData = _store.get(msg.getKey());
                    if (optData.isPresent()) {
                        Data<?> d = optData.get();
                        VectorClock v;
                        if (d.isConflict()) {
                            v = new VectorClock();
                            for (Data<?> item : d.getConflictData()) {
                                v.update(item.getVersion());
                            }
                        } else
                            v = d.getVersion().increment(getId());

                        Data dataUp = new Data<>(msg.getKey(),
                                _ch.doHash(msg.getKey()),
                                msg.getValue(), v);
                        _store.update(dataUp);
                        sendBackup(new MessageManage(this, MSG_OPERATION.UPDATE, dataUp));
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.OK));

                    } else
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.ERROR, MessageResponse.KEY_NOT_FOUND));
                    break;
            }
        } catch (SendException ignore) {

        }
    }


    private void sendBackup(MessageManage msg) {
        //TODO: not enough
        List<Node> list = _ch.getNext(msg.getData().getKey(), _replica);
        System.out.println("send propagate to.." + list);
        list.parallelStream().filter(node -> !node.equals(this)).forEach(node -> {
            try {
                node.send(msg);
            } catch (SendException ignore) {
            }
        });
    }

    public void shutdown() {
        _gossipManager.shutdown();
        _server.close();
        _store.close();
//        try {
//            synchronized (_passiveThread) {
        //_passiveThread.wait();
//            }
//            System.out.println("after join");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private void callback(GossipMember member, GossipState state) {
        //System.out.println(getId() + ". new member " + member + " with state " + state);
        if (!_toStop.get()) {
            if (!member.getId().contains(GossipResource.FRONT_ID)) {
                Node n = new Node(member);
                if (state.equals(GossipState.UP)) {
                    String info = getId() + ". NEW MEMBER [" + n + "] up ... ";
                    _ch.add(n);

                    if (_ch.getPrev(toString()).equals(n)) {
                        //TODO: prev of which replica?
                        _store.getMap().entrySet().parallelStream()
                                .filter(dataEntry -> _ch.get(dataEntry.getValue().getKey()).equals(n) /*&& _ch.getNext(dataEntry.getValue().getKey()).equals(this)*/)
                                .forEach(dataEntry -> {
                                    System.out.println(info + "SEND his data " + dataEntry.getValue());
                                    try {
                                        n.send(new MessageManage(this, MSG_OPERATION.ADDorUPDATE, dataEntry.getValue()));
                                    } catch (SendException ignore) {
                                    }
                                });
                    } else {
                        List<Node> next = _ch.getNext(toString(), _replica);
                        //TODO: next of which replica?
                        if (next.stream().anyMatch(node -> node.equals(n))) {
                            _store.getMap().entrySet().parallelStream()
                                    .filter(entry -> _ch.get(entry.getValue().getKey()).equals(this))
                                    .forEach(dataEntry -> {
                                        System.out.println(info + "SEND backup " + dataEntry.getValue());
                                        try {
                                            n.send(new MessageManage(this, MSG_OPERATION.ADDorUPDATE, dataEntry.getValue()));
                                        } catch (SendException ignore) {
                                        }
                                    });
                        }
                    }

                    //List<Long> hashList = _ch.getHashesForKey(n.toString());

                } else
                    _ch.remove(n);
            }
        }
    }
//
//    @Override
//    public String toString() {
//        return "StorageNode{" +
//                //"_ch=" + _ch +
//                ", _replica=" + _replica +
//                //", _store=" + _store +
//                "} " + super.toString();
//    }
}