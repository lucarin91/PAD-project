package lr;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.StartupSettings;
import com.google.code.gossip.event.GossipState;
import com.google.code.gossip.manager.random.RandomGossipManager;
import org.apache.log4j.Logger;

import com.google.code.gossip.event.GossipListener;
import com.google.code.gossip.manager.GossipManager;

public class Node {

    public static final Logger LOGGER = Logger.getLogger(GossipService.class);

    private GossipManager _gossipManager;
    private ConsistentHash<GossipMember> _ch;
    private Thread _passiveThread;
    private AtomicBoolean _toStop;

//    public Node(StartupSettings startupSettings) throws InterruptedException, UnknownHostException {
//        this(InetAddress.getLocalHost().getHostAddress(), startupSettings.getPort(), "",
//                startupSettings.getLogLevel(), startupSettings.getGossipMembers(), startupSettings
//                        .getGossipSettings(), this::callback);
//    }

    public Node(String ipAddress, int port, String id, int logLevel, List<GossipMember> gossipMembers, GossipSettings settings)
            throws InterruptedException, UnknownHostException {
        _gossipManager = new RandomGossipManager(ipAddress, port, id, settings, gossipMembers, this::callback);
        _ch = new ConsistentHash<>();
        _toStop = new AtomicBoolean(false);
        _passiveThread = new Thread(this::passiveRequest);
        _passiveThread.start();
    }

    public void start() {
        _gossipManager.start();
    }

    private void passiveRequest(){
        while (!_toStop.get()){
             //receive read or write request from other node
        }
    }


    public void shutdown(){
        _gossipManager.shutdown();

        _toStop.set(true);
        try {
            _passiveThread.join();
            System.out.println("after join");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public GossipManager get_gossipManager() {
        return _gossipManager;
    }

    public void set_gossipManager(GossipManager _gossipManager) {
        this._gossipManager = _gossipManager;
    }

    private void callback(GossipMember member, GossipState state){
            System.out.println(member + " " + state);
            _ch.add(member);
    }
}
