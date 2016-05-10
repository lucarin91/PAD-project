package lr.core.CmdArgs;

import com.beust.jcommander.Parameter;
import com.google.code.gossip.GossipMember;
import com.google.code.gossip.RemoteGossipMember;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by luca on 08/05/16.
 */
public class NodeArgs extends BaseArgs {

    @Parameter(names = "-id", description = "the id of the server")
    private String id = "single";

    @Parameter(names = "-ip", description = "the ip of the server")
    private String ip = "127.0.0.1";

    @Parameter(names = "-p", description = "the port of the server")
    private int port = 2000;

    @Parameter(names = "-m", description = "other servers")
    private List<String> stringMembers = new ArrayList<>();

    @Parameter(names = "-h", description = "all the configuration of the server as <id>:<ip>:<port>")
    private String host = "";

    private Optional<String[]> parseHost() {
        String[] hs = host.split(":");
        if (hs.length == 3)
            return Optional.of(hs);
        return Optional.empty();
    }

    public String getIp() {
        Optional<String[]> hosts = parseHost();
        if (hosts.isPresent())
            return hosts.get()[1];
        return ip;
    }

    public int getPort() {
        Optional<String[]> hosts = parseHost();
        if (hosts.isPresent())
            return Integer.parseInt(hosts.get()[2]);
        return port;
    }

    private static List<GossipMember> gossipMembers = null;

    public List<GossipMember> getMembers() {
        if (gossipMembers != null) return gossipMembers;
        gossipMembers = new ArrayList<>();
        for (String item : stringMembers) {
            String[] m = item.split(":");
            System.out.println(Arrays.toString(m));
            if (m.length == 3)
                gossipMembers.add(new RemoteGossipMember(m[1], Integer.parseInt(m[2]), m[0]));
        }
        return gossipMembers;
    }

    public String getId() {
        Optional<String[]> hosts = parseHost();
        if (hosts.isPresent())
            return hosts.get()[0];
        return id;
    }
}
