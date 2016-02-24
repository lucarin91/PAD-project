package lr;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.StartupSettings;
import com.google.code.gossip.event.GossipListener;

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by luca on 24/02/16.
 */
public class ConsistentGossipService extends GossipService {

    public ConsistentGossipService(StartupSettings startupSettings) throws InterruptedException, UnknownHostException {
        super(startupSettings);
    }

    public ConsistentGossipService(String ipAddress, int port, String id, int logLevel, List<GossipMember> gossipMembers, GossipSettings settings, GossipListener listener) throws InterruptedException, UnknownHostException {
        super(ipAddress, port, id, logLevel, gossipMembers, settings, listener);
    }
}
