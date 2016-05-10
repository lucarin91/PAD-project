package lr.app;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;
//import lr.gossip.GossipMember;
//import lr.gossip.RemoteGossipMember;

import lr.core.Nodes.GossipResource;
import lr.api.FrontAPI;
import lr.core.Helper;
import lr.core.Nodes.StorageNode;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.pattern.LogEvent;

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

        Logger.getRootLogger().setLevel(Level.OFF);
        Logger.getLogger("lr").setLevel(Level.INFO);

        AppArgs s = new AppArgs();
        Helper.parseArgs(s, args);

        final Map<String, StorageNode> clients = new HashMap<>();
        final List<GossipMember> startupMembers = new ArrayList<>();
        int lastServerID = s.getN() + 1;
        try {
            for (int i = 2; i < s.getSeeds() + 2; i++) {
                startupMembers.add(new RemoteGossipMember("127.0.0." + i, 2000, i + ""));
            }

            FrontAPI.start("rest", "127.0.0.1", s.getPortG(), s.getPortM(), startupMembers);

            for (int i = 2; i <= lastServerID; i++) {
                clients.put(i + "", new StorageNode(i + "", "127.0.0." + i, 2000, startupMembers)
                        .setNBackup(s.getReplica())
                        .start());
            }

            Thread.sleep(10000);

            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("\nEnter a command (h for help):");
                String read = br.readLine();
                if (read != null) {
                    String[] cmd = read.split("\\s+");
                    StorageNode n;
                    switch (cmd[0]) {
                        case "h":
                            System.out.println("COMMANDS:\n" +
                                    "\tadd <id>\tadd a new node\n" +
                                    "\trm <id>\t\tremove a node\n" +
                                    "\tq\t\tquit\n" +
                                    "\th\t\tfor this help");
                            break;
                        case "q":
                            System.exit(0);
                            break;
                        case "add":
                            int id = cmd.length == 1 ? ++lastServerID : Integer.parseInt(cmd[1]);
                            try {
                                n = new StorageNode("" + id, "127.0.0." + id, 2000, startupMembers)
                                        .setNBackup(s.getReplica())
                                        .start();
                                clients.put("" + id, n);
                                System.out.println("add server " + n);
                                System.out.println(clients);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "rm":
                            if (cmd.length > 1) {
                                n = clients.get(cmd[1]);
                                clients.remove(cmd[1]);
                                n.shutdown();
                                System.out.println("remove server " + n);
                                System.out.println(clients);
                            } else {
                                System.out.println("not founded");
                            }
                            break;
                        case "ls":
                            clients.forEach((s1, storageNode) -> System.out.println(storageNode));
                            break;
                    }
                }
            }

        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            clients.values().forEach(StorageNode::shutdown);
            GossipResource.getInstance().ifPresent(GossipResource::shutdown);
        }
    }
}

