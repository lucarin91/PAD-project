package lr;

import com.google.code.gossip.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca on 24/02/16.
 */

public class PadFs {
    public static void main(String[] args) throws IOException {

        final List<NodeService> clients = new ArrayList<>();
        try {
            System.out.println("Hello World!");

            GossipSettings settings = new GossipSettings();
            int seedNodes = 3;
            List<GossipMember> startupMembers = new ArrayList<>();
            for (int i = 1; i < seedNodes + 1; ++i) {
                startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
            }

            int clusterMembers = 5;
            for (int i = 1; i < clusterMembers + 1; ++i) {
                //final ConsistentHash<GossipMember> ch = new ConsistentHash<>();
                NodeService NodeService = new NodeService("127.0.0." + i, 2000, i + "", LogLevel.DEBUG, startupMembers, settings);
                clients.add(NodeService);
                NodeService.start();
            }


//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                public void run() {
//                    System.out.println("shotdownhook - STOP!");
//                    //clients.forEach(NodeService::shutdown);
//                }
//            });

            Thread.sleep(10000);


            clients.get(0).addData(new Data<>("test1", "come va?"));

            //Thread.sleep(10000);
    /*
            for (int i = 0; i < clusterMembers; ++i) {
                List<LocalGossipMember> list = clients.get(i).get_gossipManager().getMemberList();
                System.out.println(list.get(0).getAddress() + " "+ list.size());
            }
*/
            clients.forEach(NodeService::printStatus);

        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
            clients.forEach(NodeService::shutdown);
        }

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter command: ");
            String s = br.readLine();
            if (s.equals("q")) System.exit(0);
            if (s.equals("add")) clients.get(2).addData(new Data<>("test2", "asdasdsad"));
        }
    }
}
