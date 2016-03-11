package lr.app;

import com.beust.jcommander.JCommander;
import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;
import lr.core.CmdArgs;
import lr.core.GossipResource;
import lr.api.FrontAPI;
import lr.core.NodeService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
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
        CmdArgsApp s = new CmdArgsApp();
        new JCommander(s, args);

        final Map<String, NodeService> clients = new HashMap<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        int lastServerID = s.getN() + 1;
        try {
            for (int i = 2; i < s.getSeeds() + 2; i++) {
                startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
            }

            FrontAPI.start("rest", "127.0.0.1", s.getPortG(), s.getPortM(), startupMembers);

            for (int i = 2; i <= lastServerID; i++) {
                clients.put(i + "", new NodeService(i + "", "127.0.0." + i, 2000, startupMembers));
            }

            Thread.sleep(10000);

            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("\nEnter command: ");
                String read = br.readLine();
                if (read != null) {
                    String[] cmd = read.split("\\s+");
                    switch (cmd[0]) {
                        case "quit":
                            System.exit(0);
                            break;
                        case "add":
                            int id = cmd.length == 1 ? ++lastServerID : Integer.parseInt(cmd[1]);
                            try {
                                NodeService n = new NodeService("" + id, "127.0.0." + id, 2000, startupMembers);
                                clients.put("" + id, n);
                                System.out.println(n);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "rm":
                            if (cmd.length > 1) {
                                clients.get(cmd[1]).shutdown();
                                System.out.print(clients);
                            } else {
                                System.out.print("not founded");
                            }
                            break;
                    }
                }
            }

        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            clients.values().forEach(NodeService::shutdown);
            GossipResource.getInstance().ifPresent(GossipResource::shutdown);
        }
    }
}

