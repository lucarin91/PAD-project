package lr;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by luca on 01/03/16.
 */
@RestController
public class NodeController {

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        Optional<RestNode> r = RestNode.getInstance();
        if (r.isPresent())
            return r.get().getRandomNode().getId();
        else
            return "";
    }
}


