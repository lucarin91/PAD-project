package lr;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.code.gossip.*;
import lr.front_end.GossipResource;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luca on 24/02/16.
 */
@SpringBootApplication
public class PadFs {
    public static void main(String[] args) throws IOException {

        final Map<String,NodeService> clients = new HashMap<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        final int seedNodes = 5;
        int clusterMembers = 5;
        try {
            for (int i = 1; i < seedNodes + 1; ++i) {
                startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
            }

            for (int i = 1; i < clusterMembers + 1; ++i) {
                clients.put(i+"", new NodeService("127.0.0." + i, 2000, i + "", startupMembers));
            }

            GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, startupMembers);

            SpringApplication app = new SpringApplication(PadFs.class);
            app.setBannerMode(Banner.Mode.OFF);
            app.run(args);

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
            clients.values().forEach(NodeService::shutdown);
            //r.shoutdown();
        }

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("\nEnter command: ");
            String s = br.readLine();
            String[] cmd = s.split("\\s+");
            switch (cmd[0]) {
                case "quit":
                    System.exit(0);
                    break;
                case "add":
                    int id = cmd.length == 1 ? ++clusterMembers : Integer.parseInt(cmd[1]);
                    try {
                        NodeService n = new NodeService("127.0.0." + id, 2000, ""+id, startupMembers);
                        clients.put(""+id, n);
                        System.out.println(n);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "rm":
                    if (cmd.length>1){
                        clients.get(cmd[1]).shutdown();
                        System.out.print(clients);
                    }else{
                        System.out.print("not founded");
                    }
                    break;
            }
        }
    }

    /**
     * Strange, this needs to be here, otherwise the jdk8 module isn't auto loaded by the Jackson auto config?!
//     */
//    //@Bean
//    public Jdk8Module jdk8Module() {
//        return new Jdk8Module();
//    }
}

