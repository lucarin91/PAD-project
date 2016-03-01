package lr;

import com.google.code.gossip.GossipMember;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.LocalGossipMember;
import com.google.code.gossip.manager.GossipManager;
import com.google.code.gossip.manager.random.RandomGossipManager;

/**
 * Created by luca on 01/03/16.
 */

//@SpringBootApplication
public class RestNode extends Node {

    private static RestNode _r;
    private GossipManager _gossipManager;
    private Random _random;

    public Node getRandomNode(){
        List<LocalGossipMember> list = _gossipManager.getMemberList();
        return new Node(list.get(_random.nextInt(list.size())));
    }

    private RestNode(String id, String ip, int port, List<GossipMember> gossipMembers) {
        super(id, ip, port);
        _gossipManager = new RandomGossipManager(ip, port, id,  new GossipSettings(), gossipMembers, null);
        _gossipManager.start();
        _random = new Random();
    }

    public static RestNode getInstance(String id, String ip, int port, List<GossipMember> gossipMembers) {
        if (_r == null) _r = new RestNode(id,ip,port,gossipMembers);
        return _r;
    }

    public static Optional<RestNode> getInstance(){
        return Optional.of(_r);
    }


    @Override
    public void shutdown() {
        _gossipManager.shutdown();
    }
}
