package lr.api;

import com.google.code.gossip.GossipMember;
import lr.core.GossipResource;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca on 10/03/16.
 */

@SpringBootApplication
public class FrontAPI {
    public static void main(String[] args) {
       start("127.0.0.20", 2000, new ArrayList<>());
    }

    public static void start(String ip, int port, List<GossipMember> members) {
        GossipResource.getInstance("rest", ip, port, members);
        SpringApplication app = new SpringApplication(FrontAPI.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run();
    }
}
