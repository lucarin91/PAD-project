package lr.api;

//import com.google.code.gossip.GossipMember;
import lr.gossip.GossipMember;

import lr.core.CmdArgs;
import lr.core.Nodes.GossipResource;
import lr.core.Helper;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * Created by luca on 10/03/16.
 */
@SpringBootApplication
public class FrontAPI {
    public static void main(String[] args) {
        CmdArgs s = new CmdArgs();
        Helper.parseArgs(s,args);

        start(s.getId(), s.getIp(), s.getPort(), s.getMembers());
    }

    public static void start(String id, String ip, int port, List<GossipMember> members) {
        start(id,ip,port,port+1,members);
    }

    public static void start(String id, String ip, int portG, int portM, List<GossipMember> members) {
        GossipResource.getInstance(id, ip, portG, portM, members);
        SpringApplication app = new SpringApplication(FrontAPI.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run();
    }
}
