package lr;

import com.google.code.gossip.*;
import lr.front_end.GossipResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca on 24/02/16.
 */
@SpringBootApplication
public class PadFs {
    public static void main(String[] args) throws IOException {

        final List<NodeService> clients = new ArrayList<>();
        try {
            System.out.println("Hello World!");

            GossipSettings settings = new GossipSettings();
            int seedNodes = 5;
            List<GossipMember> startupMembers = new ArrayList<>();
            for (int i = 1; i < seedNodes + 1; ++i) {
                startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
            }

            int clusterMembers = 5;
            for (int i = 1; i < clusterMembers + 1; ++i) {
                NodeService NodeService = new NodeService("127.0.0." + i, 2000, i + "", LogLevel.DEBUG, startupMembers, settings);
                clients.add(NodeService);
            }
            GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, startupMembers);

            SpringApplication.run(PadFs.class);

            Thread.sleep(10000);

            /*
            for (int i = 0; i < clusterMembers; ++i) {
                List<LocalGossipMember> list = clients.get(i).get_gossipManager().getMemberList();
                System.out.println(list.get(0).getAddress() + " "+ list.size());
            }
            */

            //clients.forEach(NodeService::printStatus);

        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
            clients.forEach(NodeService::shutdown);
            //r.shoutdown();
        }

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter command: ");
            String s = br.readLine();
            if (s.equals("q")) System.exit(0);
        }
    }
}

