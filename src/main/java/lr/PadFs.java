package lr;

import com.google.code.gossip.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca on 24/02/16.
 */

public class PadFs {
    public static void main(String[] args) {
        try {
            System.out.println("Hello World!");

            GossipSettings settings = new GossipSettings();
            int seedNodes = 3;
            List<GossipMember> startupMembers = new ArrayList<>();
            for (int i = 1; i < seedNodes + 1; ++i) {
                startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
            }

            List<NodeService> clients = new ArrayList<>();
            int clusterMembers = 5;
            for (int i = 1; i < clusterMembers + 1; ++i) {
                //final ConsistentHash<GossipMember> ch = new ConsistentHash<>();
                NodeService NodeService = new NodeService("127.0.0." + i, 2000, i + "", LogLevel.DEBUG, startupMembers, settings);
                clients.add(NodeService);
                NodeService.start();
            }

            Thread.sleep(10000);
            for (int i = 0; i < clusterMembers; ++i) {
                List<LocalGossipMember> list = clients.get(i).get_gossipManager().getMemberList();
                System.out.println(list.get(0).getAddress() + " "+ list.size());
            }

            clients.forEach(NodeService::printStatus);
            clients.forEach(NodeService::shutdown);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
