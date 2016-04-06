package lr.core.Nodes;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

//import com.google.code.gossip.*;
import lr.gossip.*;
import lr.gossip.event.GossipState;
import lr.gossip.manager.random.*;
import lr.gossip.manager.*;

import lr.core.*;
import lr.core.Exception.SendException;
import lr.core.Messages.*;
import lr.core.Messages.Message.*;


public class StorageNode extends Node {

    private LoggerWrapper LOG;

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

        LOG = new LoggerWrapper(StorageNode.class, id_);

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

                LOG.i("receive.. " + msg);

                if (msg instanceof MessageRequest<?>) {

                    MessageRequest msgR = (MessageRequest) msg;
                    if (msgR.getOperation() != MSG_OPERATION.STATUS) {
                        //msg.setSender(this);
                        Node n = _ch.get(msgR.getKey());

                        if (n.getId().equals(getId())) {
                            doOperation(msgR);
                        } else {
                            LOG.i("pass message..");
                            n.send(msg);
                        }

                    } else {
                        msgR.getSender().send(new MessageStatus(this, _store.getMap(), _ch.getMap()));
                    }
                } else if (msg instanceof MessageManage) {
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
        LOG.i("do Management Operation with.. " + msg.getData());
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
                Optional<Data<?>> optData = _store.get(msg.getData().getKey());
                if (optData.isPresent()) {
                    Data<?> thisData = optData.get();
                    if (thisData.isConflict() || thatData.isConflict()) {
                        thisData.addConflict(thatData);
                        _store.update(thatData);
                    }else {
                        VectorClock thisClock = thisData.getVersion();
                        VectorClock thatClock = thatData.getVersion();
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
                    }
                } else
                    _store.add(msg.getData());
                break;
        }
    }

    private void doOperation(MessageRequest<?> msg) {
        LOG.i("do Operation with.. " + msg.getKey());
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
        List<Map.Entry<Long, Node>> list = _ch.getNext(msg.getKey(), _replica + 1);
        LOG.i("send backup to.. " + list);
        list.parallelStream().filter(node -> !node.equals(this)).forEach(entry -> {
            try {
                entry.getValue().send(msg);
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
                    LOG.i("new member [" + member + "] up {live: "+ _gossipManager.getMemberList() +"- death: " + _gossipManager.getDeadList() +  "}");
                    _ch.add(n);

                    _ch.getReplicaForKey(toString()).parallelStream().forEach(repHash -> {

                        //check if new node is prev of replica
                        ArrayList<Map.Entry<Long, Node>> preEntry = _ch.getPrev(repHash, 2);
                        Set<Data<?>> dataSet = new HashSet<>();

                        if (preEntry.get(0).getValue().equals(n)) {
                            dataSet.addAll(_store.getInterval(preEntry.get(1).getKey(), preEntry.get(0).getKey()));
                            LOG.i("send his data " + dataSet);
                        } else {

                            ArrayList<Map.Entry<Long, Node>> nextMap = _ch.getNext(repHash, _replica);

                            if (nextMap.stream().anyMatch(entry -> entry.getValue().equals(n))) {
                                dataSet.addAll(_store.getInterval(_ch.getPrev(repHash).getKey(), repHash));
                                LOG.i("send backup data " + dataSet);
                            }
                        }

                        dataSet.parallelStream().forEach(data -> {
                            try {
                                n.send(new MessageManage(this, MSG_OPERATION.ADDorUPDATE, data));
                            } catch (SendException ignore) {
                            }

                        });

                    });

                } else {
                    LOG.i("del member [" + member + "] down.. ");
                    _ch.remove(n);
                }
            }
        }
    }
}
