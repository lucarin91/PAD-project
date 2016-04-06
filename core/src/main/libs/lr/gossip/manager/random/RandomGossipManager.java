package lr.gossip.manager.random;

import lr.gossip.GossipMember;
import lr.gossip.GossipSettings;
import lr.gossip.event.GossipListener;
import lr.gossip.manager.GossipManager;
import lr.gossip.manager.impl.OnlyProcessReceivedPassiveGossipThread;

import java.util.List;

public class RandomGossipManager extends GossipManager {
  public RandomGossipManager(String address, int port, String id, GossipSettings settings,
                             List<GossipMember> gossipMembers, GossipListener listener) {
    super(OnlyProcessReceivedPassiveGossipThread.class, RandomActiveGossipThread.class, address,
            port, id, settings, gossipMembers, listener);
  }
}
