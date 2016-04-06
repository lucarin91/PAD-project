package lr.gossip.event;

import lr.gossip.GossipMember;

public interface GossipListener {
  void gossipEvent(GossipMember member, GossipState state);
}
