package lr.gossip;

import lr.gossip.event.GossipListener;
import lr.gossip.manager.GossipManager;
import lr.gossip.manager.random.RandomGossipManager;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * This object represents the service which is responsible for gossiping with other gossip members.
 *
 * @author joshclemm, harmenw
 */
public class GossipService {

  public static final Logger LOGGER = Logger.getLogger(GossipService.class);

  private GossipManager _gossipManager;

  /**
   * Constructor with the default settings.
   *
   * @throws InterruptedException
   * @throws UnknownHostException
   */
  public GossipService(StartupSettings startupSettings) throws InterruptedException,
          UnknownHostException {
    this(InetAddress.getLocalHost().getHostAddress(), startupSettings.getPort(), "",
             startupSettings.getGossipMembers(), startupSettings
                    .getGossipSettings(), null);
  }

  /**
   * Setup the client's lists, gossiping parameters, and parse the startup config file.
   *
   * @throws InterruptedException
   * @throws UnknownHostException
   */
  public GossipService(String ipAddress, int port, String id,
                       List<GossipMember> gossipMembers, GossipSettings settings, GossipListener listener)
          throws InterruptedException, UnknownHostException {
    _gossipManager = new RandomGossipManager(ipAddress, port, id, settings, gossipMembers, listener);
  }

  public void start() {
    _gossipManager.start();
  }

  public void shutdown() {
    _gossipManager.shutdown();
  }

  public GossipManager get_gossipManager() {
    return _gossipManager;
  }

  public void set_gossipManager(GossipManager _gossipManager) {
    this._gossipManager = _gossipManager;
  }

}
