package lr.app;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;
import lr.core.GossipResource;
import lr.api.FrontAPI;
import lr.core.NodeService;

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

public class App {
    public static void main(String[] args) throws IOException {

        final Map<String,NodeService> clients = new HashMap<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        final int seedNodes = 3;
        int clusterMembers = 5;
        try {
            for (int i = 1; i < seedNodes + 1; ++i) {
                startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
            }

            for (int i = 1; i < clusterMembers + 1; ++i) {
                clients.put(i+"", new NodeService("127.0.0." + i, 2000, i + "", startupMembers));
            }

            FrontAPI.start("127.0.0.20", 2000, startupMembers);

            Thread.sleep(10000);

            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("\nEnter command: ");
                String s = br.readLine();
                String[] cmd = s!=null ? s.split("\\s+") : new String[]{"quit"};
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

        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        }finally {
            clients.values().forEach(NodeService::shutdown);
            GossipResource.getInstance().ifPresent(GossipResource::shutdown);
        }
    }
}

