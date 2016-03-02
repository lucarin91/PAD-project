package lr.front_end;

import lr.Data;
import lr.Message;
import lr.Node;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Optional;

/**
 * Created by luca on 01/03/16.
 */
@RestController
@RequestMapping("/")
public class NodeController {

    @RequestMapping(method = RequestMethod.GET)
    public Data get(@RequestParam(value = "key") String key) {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            Node n = r.getRandomNode();
            n.send(new Message(Message.MSG_TYPE.GET, Message.SENDER_TYPE.FRONT, r.getIp(), r.getPortM(), new Data(key)));

            return r.receive().get().getData();
        } else
            return null;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody Data data) {
        Optional<GossipResource> r = GossipResource.getInstance();
        if (r.isPresent()) {
            Node n = r.get().getRandomNode();
            n.send(new Message(Message.MSG_TYPE.ADD, Message.SENDER_TYPE.FRONT,data));
            return "send to "+ n.getId();
        }
        return "error";
    }
}


