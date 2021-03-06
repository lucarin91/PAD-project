package lr.core.Nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.google.code.gossip.*;
import com.google.code.gossip.manager.GossipManager;
import com.google.code.gossip.manager.random.RandomGossipManager;
//import lr.gossip.*;
//import lr.gossip.manager.GossipManager;
//import lr.gossip.manager.random.RandomGossipManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


import lr.core.Exception.SendException;
import lr.core.Messages.Message;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageResponse;

/**
 * Created by luca on 01/03/16.
 */
public class GossipResource extends Node {
    public final static String FRONT_ID = "[REST-FRONT]";

    private static GossipResource _r;
    @JsonIgnore
    private GossipManager _gossipManager;
    private DatagramSocket _server;
    private Random _random;

    @JsonIgnore
    public Optional<Node> getRandomNode() {
        List<LocalGossipMember> list = _gossipManager.getMemberList();
        if (list.size() > 0) {
            LocalGossipMember member;
            while ((member = list.get(_random.nextInt(list.size()))).getId().contains(GossipResource.FRONT_ID));
            return Optional.of(new Node(member));
        } else
            return Optional.empty();
    }

    public int sendToNodes(MessageRequest msg) {
        List<LocalGossipMember> list = _gossipManager.getMemberList();
        int N = 0;
        for (LocalGossipMember n : list) {
            try {
                send(new Node(n), msg);
                N++;
            } catch (SendException e) {
                e.printStackTrace();
            }
        }
        return N;
    }

    private GossipResource(String id, String ip, int portG, int portM, List<GossipMember> gossipMembers) {
        super(id, ip, portG, portM);

        try {
            _server = new DatagramSocket(new InetSocketAddress(ip, portM));
            _server.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        _gossipManager = new RandomGossipManager(ip, portG, FRONT_ID + " " + id, new GossipSettings(), gossipMembers, (member, state) -> LOG.i(member + " "+ state));
        _gossipManager.start();
        _random = new Random();
    }

    @JsonIgnore
    public static GossipResource getInstance(String id, String ip, int port, List<GossipMember> gossipMembers) {
        return getInstance(id, ip, port, port + 1, gossipMembers);
    }

    @JsonIgnore
    public static GossipResource getInstance(String id, String ip, int portG, int portM, List<GossipMember> gossipMembers) {
        if (_r == null) _r = new GossipResource(id, ip, portG, portM, gossipMembers);
        return _r;
    }

    @JsonIgnore
    public static Optional<GossipResource> getInstance() {
        return Optional.of(_r);
    }

    public <T extends Message> Optional<T> receive() {
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
            return Optional.of(mapper.readValue(receivedMessage, new TypeReference<T>() {
            }));

        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    public static MessageResponse<?> sendRequestToRandomNode(MessageRequest<?> req) throws SendException {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            Optional<Node> opt_n;
            if ((opt_n = r.getRandomNode()).isPresent()) {
                req.setSender(r);
                r.send(opt_n.get(), req);
                Optional<MessageResponse<?>> responseOptional = r.<MessageResponse<?>>receive();
                if (responseOptional.isPresent()) {
                    return responseOptional.get();
                } else
                    throw new SendException("nothing received back");
            } else
                throw new SendException("no storage node found");
        } else
            throw new SendException("GossipResource is not initialized");
    }

    @Override
    public void shutdown() {
        _gossipManager.shutdown();
        _server.close();
        _r = null;
    }
}
