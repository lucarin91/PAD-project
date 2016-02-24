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

            List<GossipService> clients = new ArrayList<>();
            int clusterMembers = 5;
            for (int i = 1; i < clusterMembers + 1; ++i) {
                GossipService gossipService = null;
                final int finalI = i;
                gossipService = new GossipService("127.0.0." + i, 2000, i + "", LogLevel.DEBUG, startupMembers, settings,
                        (member, state) -> System.out.println("id" + finalI + " - " + member + " " + state));
                clients.add(gossipService);
                gossipService.start();
            }

            Thread.sleep(1000);
            for (int i = 0; i < clusterMembers; ++i) {
                List<LocalGossipMember> list = clients.get(i).get_gossipManager().getMemberList();
                System.out.println(list.get(0).getAddress());
                System.out.println(list.size());
            }

            clients.forEach(GossipService::shutdown);

        } catch (InterruptedException e) {
            System.out.print(e);
        } catch (UnknownHostException e) {
            System.out.print(e);
        } finally {
            System.exit(0);
        }
    }
}
