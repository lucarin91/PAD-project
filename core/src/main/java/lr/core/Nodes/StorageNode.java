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

                String receivedMessage = new String(buf);

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

            case UP:
                VectorClock thatClock = msg.getData().getVersion();
                _store.get(msg.getData().getKey()).ifPresent(thisData -> {
                    VectorClock thisClock = thisData.getVersion();
                    switch (thisClock.compareTo(thatClock)) {
                        case BEFORE:
                            _store.update(msg.getData());
                            break;
                        case NOTHING:
                            BinaryOperator<Long> sum = (a, b) -> a + b;
                            long thisCounter = thisClock.getVector().values().parallelStream().reduce(sum).get();
                            long thatCounter = thatClock.getVector().values().parallelStream().reduce(sum).get();
                            if (thisCounter < thatCounter)
                                _store.update(msg.getData());
                            break;
                        case AFTER:
                        case EQUAL:
                    }
                });
                break;

            case DEL:
                _store.remove(msg.getKey());
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

                case DEL:
                    if (_store.remove(msg.getKey())) {
                        sendBackup(new MessageManage(this, MSG_OPERATION.DEL, msg.getKey()));
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.OK));
                    } else {
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.ERROR, MessageResponse.KEY_NOT_FOUND));
                    }
                    break;

                case UP:
                    Optional<Data<?>> data2 = _store.get(msg.getKey());
                    if (data2.isPresent()) {
                        Data dataUp = new Data<>(msg.getKey(),
                                _ch.doHash(msg.getKey()),
                                msg.getValue(),
                                data2.get().getVersion().increment(getId()));
                        _store.update(dataUp);
                        sendBackup(new MessageManage(this, MSG_OPERATION.UP, dataUp));
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.OK));
                    } else
                        sender.send(new MessageResponse<>(this, MessageResponse.MSG_STATUS.ERROR, MessageResponse.KEY_NOT_FOUND));
                    break;
            }
        } catch (SendException ignore) {

        }
    }


    private void sendBackup(MessageManage msg) {
        List<Node> list = _ch.getNext(toString(), _replica);
        System.out.println("send propagate to.." + list);
        for (Node n : list) {
            try {
                n.send(msg);
            } catch (SendException ignore) {
            }
        }
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
        //System.out.println(member + " " + state);
        if (!_toStop.get()) {
            if (!member.getId().contains(GossipResource.FRONT_ID)) {
                Node n = new Node(member);
                if (state.equals(GossipState.UP)) {
                    String info = getId() + ". NEW MEMBER [" + n + "] up ...";
                    _ch.add(n);
                    List<Node> next = _ch.getNext(toString(), _replica);
                    if (next.stream().anyMatch(node -> node.equals(n))) {
                        System.out.println(info + "SEND backup data [" + next + "]");
                        _store.getMap().forEach((s, data) -> {
                            try {
                                n.send(new MessageManage(this, MSG_OPERATION.ADD, data));
                            } catch (SendException ignore) {
                            }
                        });
                    }

                    List<Long> hashList = _ch.getHashesForKey(n.toString());
                    _store.getMap().entrySet().parallelStream()
                            .filter(dataEntry -> hashList.stream().anyMatch(hash -> _ch.doHash(dataEntry.getValue().getKey()) < hash))
                            .forEach(dataEntry -> {
                                System.out.println(info + "SEND his data: " + dataEntry.getKey() + "-" + dataEntry.getValue());
                                try {
                                    n.send(new MessageManage(this, MSG_OPERATION.ADD, dataEntry.getValue()));
                                } catch (SendException ignore) {
                                }
                            });
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
