package lr.front_end;

import lr.Data;
import lr.Messages.MessageManage;
import lr.Messages.Message.*;
import lr.Node;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by luca on 01/03/16.
 */
@RestController
@RequestMapping("/api")
public class PublicAPI {

    @RequestMapping(method = RequestMethod.GET)
    public Data get(@RequestParam(value = "key") String key) {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            Node n = r.getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.GET, r, new Data(key)));

            return r.<MessageManage>receive().get().getData();
        } else
            return null;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody Data data) {
        Optional<GossipResource> r = GossipResource.getInstance();
        if (r.isPresent()) {
            Node n = r.get().getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.ADD, r.get() , data));
            return "send to "+ n.getId();
        }
        return "error";
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String del(@RequestParam(value = "key") String key) {
        Optional<GossipResource> r = GossipResource.getInstance();
        if (r.isPresent()) {
            Node n = r.get().getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.DEL, new Node(Node.NODE_TYPE.FRONT,r.get().getId(),r.get().getIp(), r.get().getPortG()), new Data(key)));
            return "send to "+ n.getId();
        }
        return "error";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestBody Data data) {
        Optional<GossipResource> r = GossipResource.getInstance();
        if (r.isPresent()) {
            Node n = r.get().getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.UP, r.get(), data));
            return "send to "+ n.getId();
        }
        return "error";
    }
}


