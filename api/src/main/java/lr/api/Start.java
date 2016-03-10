package lr.api;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;

import java.util.ArrayList;

/**
 * Created by luca on 10/03/16.
 */
public class Start {
    public static void main(String[] args){
        GossipResource r = GossipResource.getInstance("rest", "127.0.0.20", 2000, new ArrayList<>());

        SpringApplication app = new SpringApplication(Start.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
